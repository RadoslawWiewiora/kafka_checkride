variable "region" {
  description = "AWS Region"
  default = "eu-west-1"
}

variable "vpc" {
  description = "Name of VPC"
  default = "checkride-vpc"
}

variable "availability_zones" {
  description = "A list of availability zones for the subnets."
  type        = list(string)
  default     = ["eu-west-1a", "eu-west-1b"]
}

variable "cidr_blocks" {
  description = "A list of CIDRs for the subnets."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "subnet_count" {
  description = "Number of subnets"
  type        = number
  default     = 2
}