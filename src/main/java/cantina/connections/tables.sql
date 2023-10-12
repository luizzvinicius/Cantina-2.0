CREATE DATABASE IF NOT EXISTS cantina;
USE cantina;
CREATE TABLE IF NOT EXISTS funcionario (
    id INT(8) AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(256) UNIQUE,
    senha VARCHAR(256) NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS produto (
    codigo INT(8) AUTO_INCREMENT,
    id_funcionario INT(8),
    nome VARCHAR(100) NOT NULL UNIQUE,
    preco_compra Decimal(14,2) NOT NULL,
    preco_venda Decimal(14,2) NOT NULL,
    quantidade_comprada Decimal(14,2) NOT NULL,
    quantidade_vendida Decimal(14,2) NOT NULL,
    quantidade_atual Decimal(14,2) NOT NULL,
    CHECK (preco_compra >= 0),
    CHECK (preco_venda >= 0),
    CHECK (quantidade_comprada >= 0),
    CHECK (preco_venda > preco_compra),
    PRIMARY KEY (codigo),
    FOREIGN KEY (id_funcionario) REFERENCES funcionario(id)
);
CREATE TABLE IF NOT EXISTS venda (
    codigo INT(8) AUTO_INCREMENT,
    id_funcionario INT(8) NOT NULL,
    desconto Decimal(14,2) NOT NULL,
    total_venda Decimal(14,2),
    forma_pagamento VARCHAR(25),
    data TIMESTAMP NOT NULL,
    CHECK (desconto >= 0),
    PRIMARY KEY (codigo),
    FOREIGN KEY (id_funcionario) REFERENCES funcionario(id)
);
CREATE TABLE IF NOT EXISTS item_venda (
    codigo_item INT(8) AUTO_INCREMENT,
    codigo_produto INT(8),
    codigo_venda INT(8),
    preco_venda Decimal(14,2) NOT NULL,
    quantidade Decimal(14,2) NOT NULL,
    CHECK (preco_venda > 0),
    CHECK (quantidade > 0),
    PRIMARY KEY (codigo_item),
    FOREIGN KEY (codigo_produto) REFERENCES produto(codigo) ON DELETE CASCADE,
    FOREIGN KEY (codigo_venda) REFERENCES venda(codigo)
);