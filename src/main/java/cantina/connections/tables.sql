CREATE DATABASE IF NOT EXISTS cantina;
USE cantina;
create table IF NOT EXISTS funcionario (
    nome varchar(50),
    email varchar(50),
    senha varchar(50)
);
create table IF NOT EXISTS produto (
    nome varchar(50),
    preco Decimal(14,2),
    descricao varchar(50)
);