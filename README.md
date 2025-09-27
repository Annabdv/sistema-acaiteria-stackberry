<p align="center">
  <img src="./assets/StackBerry_Text.png" alt="Logo do StackBerry" width="300">
</p>

# StackBerry - Sistema de Gerenciamento para Açaiteria

*Do Stack de Tecnologia à Tigela de Açaí.*

Este projeto é um sistema web para gestão de uma açaiteria, desenvolvido com Java, Javalin e Thymeleaf. O objetivo é criar uma plataforma funcional que atenda tanto aos clientes que desejam fazer pedidos online quanto aos administradores e funcionários que gerenciam a operação da loja.

---

## Sumário

* [Visão Geral](#visão-geral)
* [Funcionalidades Planejadas (MVP)](#funcionalidades-planejadas-mvp)
* [Tecnologias](#tecnologias)
* [Design e Identidade Visual](#design-e-identidade-visual)
* [Como Executar o Projeto](#como-executar-o-projeto)
* [Membros da Equipe](#membros-da-equipe)

---

## Visão Geral

O **StackBerry** permitirá que os clientes montem seus açaís de forma personalizada, escolhendo tamanhos e adicionais. Do lado da loja, enquanto administradores terão controle total sobre o catálogo de produtos e o acesso ao sistema.

### Níveis de Acesso

O sistema conta com 2 tipos de usuários:
1.  **Cliente:** Pode se cadastrar, navegar pelo cardápio, montar e finalizar pedidos, além de acompanhar o status de entrega.
2.  **Administrador:** Possui todas as permissões de um funcionário, com o adicional de poder gerenciar (CRUD) os produtos, adicionais, estoque e usuários do sistema.

---

## Funcionalidades Planejadas (MVP)

Esta é a lista de funcionalidades essenciais para a primeira versão do projeto.

### 👤 Para Clientes
- [ ] Visualizar o cardápio com produtos, tamanhos e adicionais disponíveis.
- [ ] Montar um açaí personalizado.
- [ ] Adicionar itens ao carrinho de compras.
- [ ] Finalizar um pedido.
- [ ] Cancelar um pedido.
- [ ] Acompanhar o status do seu pedido (Ex: `Em Preparo`, `Entregue`).

### 🔑 Para Administradores
- [ ] Todas as funcionalidades de **Funcionário**.
- [ ] Gerenciar (criar, editar, deletar) produtos, como tamanhos de açaí e acompanhamentos.
- [ ] Finalizar os pedidos, alterando o status para "Entregue".
- [ ] Visualizar um dashboard com dados de vendas e pedidos.

---

## Tecnologias

Esta é a stack principal definida para o projeto.

* **Linguagem:** Java
* **Framework Backend:** [Javalin](https://javalin.io/)
* **Template Engine (View):** [Thymeleaf](https://www.thymeleaf.org/)
* **Interface e Scripts:** HTML5, CSS3 e JavaScript (com possível uso de [Bootstrap](https://getbootstrap.com/))
* **Banco de Dados:** (A ser definido)
* **Build Tool:** [Maven](https://maven.apache.org/)

---

## Design e Identidade Visual

* **Nome:** StackBerry
* **Paleta de Cores:**
    * 💜 Roxo
    * 💚 Verde
    * 🤍 Branco

---

## Como Executar o Projeto

*Instruções preliminares. Serão detalhadas após a criação da estrutura do projeto.*

**Pré-requisitos:**
* Java JDK (versão a definir)
* Maven ou Gradle

**Passos:**
1.  Clone o repositório:
    ```bash
    git clone [URL-DO-REPOSITORIO]
    ```
2.  Navegue até a pasta do projeto e execute o comando de build/run.
    ```bash
    # Exemplo futuro
    mvn compile exec:java
    ```
3.  Acesse `http://localhost:7070` (ou a porta configurada) no seu navegador.

---

## Membros da Equipe

| ![Foto](https://github.com/Edgar-sh.png?size=100) | ![Foto](https://github.com/randersonranniery7.png?size=100) | ![Foto](https://github.com/Annabdv.png?size=100) |
|----------------------------------------------------|-------------------------------------------------------|-----------------------------------------------------|
| **Edgar Henrique da Silva** [@Edgar-sh](https://github.com/Edgar-sh) | **Randerson Ranniery** [@randersonranniery7](https://github.com/randersonranniery7) | **Anna Beatriz Alves de Vasconcelos** [@Annabdv](https://github.com/Annabdv) |
