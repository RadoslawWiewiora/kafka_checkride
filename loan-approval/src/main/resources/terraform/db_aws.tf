provider "aws" {
  region = var.region
}

resource "aws_vpc" "vpc" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true

  tags = {
    Name = var.vpc
  }
}

resource "aws_internet_gateway" "internet-gateway" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "IGW"
  }
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "Public Route Table"
  }
}

resource "aws_route" "public_internet_gateway" {
  route_table_id         = aws_route_table.public_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.internet-gateway.id
}

resource "aws_subnet" "public_subnet" {
  count = var.subnet_count
  vpc_id     = aws_vpc.vpc.id
  availability_zone = var.availability_zones[count.index]
  cidr_block = var.cidr_blocks[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "Public subnet ${count.index + 1}"
  }
}

resource "aws_db_subnet_group" "postgres_subnet_group" {
  name       = "postgres-db-subnet-group"
  subnet_ids = aws_subnet.public_subnet.*.id
}

resource "aws_route_table_association" "public_subnet_route_table_association" {
  count = var.subnet_count
  subnet_id = aws_subnet.public_subnet[count.index].id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_security_group" "rds_sg" {
  name        = "example_rds_sg"
  description = "RDS security group"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
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
