-- Banco: loja_roupas (PostgreSQL)
-- Antes: CREATE DATABASE loja_roupas;

DROP TABLE IF EXISTS item_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    senha_hash  VARCHAR(200) NOT NULL,
    cep         VARCHAR(8)   NOT NULL,
    logradouro  VARCHAR(200),
    bairro      VARCHAR(120),
    cidade      VARCHAR(120),
    uf          VARCHAR(2)
);

CREATE TABLE produto (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    descricao   VARCHAR(500),
    categoria   VARCHAR(30)  NOT NULL,
    tamanho     VARCHAR(10),
    cor         VARCHAR(30),
    preco       NUMERIC(10,2) NOT NULL CHECK (preco > 0),
    estoque     INTEGER       NOT NULL DEFAULT 0 CHECK (estoque >= 0)
);

CREATE TABLE pedido (
    id                   BIGSERIAL PRIMARY KEY,
    cliente_id           BIGINT        NOT NULL REFERENCES cliente (id),
    data_criacao         TIMESTAMP     NOT NULL,
    status               VARCHAR(20)   NOT NULL,
    forma_pagamento      VARCHAR(20)   NOT NULL,
    cep_entrega          VARCHAR(8)    NOT NULL,
    uf_entrega           VARCHAR(2),
    subtotal             NUMERIC(10,2) NOT NULL,
    desconto             NUMERIC(10,2) NOT NULL,
    frete                NUMERIC(10,2) NOT NULL,
    total                NUMERIC(10,2) NOT NULL,
    tipo_frete           VARCHAR(20)   NOT NULL,
    descontos_aplicados  VARCHAR(200)
);

CREATE TABLE item_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT        NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    produto_id      BIGINT        NOT NULL REFERENCES produto (id),
    nome_produto    VARCHAR(150)  NOT NULL,
    quantidade      INTEGER       NOT NULL CHECK (quantidade > 0),
    preco_unitario  NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_pedido_cliente ON pedido (cliente_id);
CREATE INDEX idx_item_pedido_pedido ON item_pedido (pedido_id);

-- Dados de exemplo (opcional)
INSERT INTO produto (nome, descricao, categoria, tamanho, cor, preco, estoque) VALUES
    ('Camiseta Básica Algodão', 'Camiseta 100% algodão, gola redonda', 'CAMISETA', 'M', 'Branca', 49.90, 50),
    ('Camiseta Estampada Surf', 'Camiseta estampada, modelagem reta',   'CAMISETA', 'G', 'Azul',   69.90, 30),
    ('Calça Jeans Slim',        'Calça jeans slim com elastano',        'CALCA',    '42', 'Azul escuro', 159.90, 20),
    ('Vestido Midi Floral',     'Vestido midi em viscose',              'VESTIDO',  'M', 'Verde',  189.90, 15),
    ('Jaqueta Corta-Vento',     'Jaqueta leve impermeável',             'JAQUETA',  'G', 'Preta',  229.90, 10),
    ('Tênis Casual',            'Tênis casual unissex',                 'CALCADO',  '40', 'Branco', 249.90, 12);
