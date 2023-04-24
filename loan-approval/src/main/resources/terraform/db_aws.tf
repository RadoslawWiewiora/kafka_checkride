provider "aws" {
  region = "eu-west-1"
}

resource "aws_vpc" "rwiew_vpc" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true

  tags = {
    Name = "rwiew_vpc"
  }
}

resource "aws_internet_gateway" "internet-gateway" {
  vpc_id = aws_vpc.rwiew_vpc.id

  tags = {
    Name = "rwiew_vpc IGW"
  }
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.rwiew_vpc.id

  tags = {
    Name = "rwiew_vpc Public Route Table"
  }
}

resource "aws_route" "public_internet_gateway" {
  route_table_id         = aws_route_table.public_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.internet-gateway.id
}

resource "aws_route_table_association" "public_subnet_1_route_table_association" {
  subnet_id = aws_subnet.public_subnet_1.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public_subnet_2_route_table_association" {
  subnet_id = aws_subnet.public_subnet_2.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_subnet" "public_subnet_1" {
  vpc_id     = aws_vpc.rwiew_vpc.id
  availability_zone = "eu-west-1a"
  cidr_block = "10.0.1.0/24"
  map_public_ip_on_launch = true
}

resource "aws_subnet" "public_subnet_2" {
  vpc_id     = aws_vpc.rwiew_vpc.id
  availability_zone = "eu-west-1b"
  cidr_block = "10.0.2.0/24"
  map_public_ip_on_launch = true
}

resource "aws_db_subnet_group" "postgres_subnet_group" {
  name       = "postgres-db-subnet-group"
  subnet_ids = [aws_subnet.public_subnet_1.id, aws_subnet.public_subnet_2.id]
}

resource "aws_security_group" "rds_sg" {
  name        = "example_rds_sg"
  description = "RDS security group"
  vpc_id      = aws_vpc.rwiew_vpc.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "postgres" {
  identifier           = "checkride-db-instance"
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "14.6"
  instance_class       = "db.t3.micro"
  username             = "postgres"
  password             = "adminadmin"
  parameter_group_name = "default.postgres14"
  multi_az             = false
  publicly_accessible  = true

  db_subnet_group_name   = aws_db_subnet_group.postgres_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  skip_final_snapshot = true

  tags = {
    Name = "postgres-db-checkride"
    Owner = "rwiew"
  }
}
