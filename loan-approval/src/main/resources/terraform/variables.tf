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