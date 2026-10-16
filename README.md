<p align="center">
  <img src="./assets/StackBerry_Text.png" alt="Logo do StackBerry" width="300">
</p>

# StackBerry - Sistema de Gerenciamento para Açaiteria

*Do Stack de Tecnologia à Tigela de Açaí.*

---

## 🎓 Contexto Acadêmico & Histórico do Projeto

- **Origem Acadêmica:** Realizei este projeto para a disciplina de **Análise e Projeto de Sistemas (APS)** na **UFPB Campus IV** (Universidade Federal da Paraíba).
- **Refatoração Visual:** Houve **refatoração completa do visual e da experiência de pedidos** por **Anna Vasconcelos** ([@Annabdv](https://github.com/Annabdv)), incluindo design system moderno, customizador interativo de tigelas, resumo de pedidos em tempo real e visual glassmorphism.
- **Nota sobre o Repositório:** O atual projeto neste repositório **não conta com os outros integrantes da disciplina por ter sido perdido ao final da disciplina** (após a exclusão do repositório original da turma). O código foi recuperado e mantido neste repositório próprio como dona do projeto.

---

## 📌 Sumário

* [Visão Geral](#visão-geral)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Funcionalidades](#funcionalidades)
* [Design e Identidade Visual](#design-e-identidade-visual)
* [Como Executar o Projeto](#como-executar-o-projeto)
* [Autoria](#autoria)

---

## 📖 Visão Geral

O **StackBerry** é um sistema web para gestão completa de uma açaiteria. Ele permite que os clientes personalizem suas tigelas de açaí escolhendo tamanhos e acompanhamentos (com cálculo dinâmico de preços), adicionem ao carrinho e façam seus pedidos com encaminhamento direto para o WhatsApp. Do lado administrativo, o sistema oferece controle sobre catálogo de produtos, adicionais e acompanhamento de pedidos.

### Níveis de Acesso
1. **Cliente:** Navegar pelo cardápio, montar tigelas personalizadas, gerenciar carrinho, finalizar pedidos e acompanhar o status em tempo real.
2. **Administrador:** Acesso a dashboard de controle, gerenciamento de produtos (tamanhos e adicionais), controle de pedidos e usuários.

---

## 🛠️ Tecnologias Utilizadas

Este projeto utiliza estritamente o stack definido originalmente, sem adição de linguagens ou frameworks externos:

* **Linguagem:** Java 21 (LTS)
* **Framework Backend:** [Javalin](https://javalin.io/) (6.7.0)
* **Template Engine:** [Thymeleaf](https://www.thymeleaf.org/) com Layout Dialect
* **Frontend & Estilização:** HTML5 semântico, Vanilla CSS3 (Design System Glassmorphism) e JavaScript / jQuery
* **Persistência:** Arquivos JSON estruturados
* **Build Tool:** [Apache Maven](https://maven.apache.org/)

---

## ✨ Funcionalidades Principais

### 👤 Para Clientes
- [x] **Cardápio Interativo:** Escolha visual de tamanhos (Pequeno, Médio, Grande) com ícones e cartões de destaque.
- [x] **Seleção de Adicionais:** Acompanhamentos organizados em chips interativos (frutas, coberturas, cremes).
- [x] **Resumo em Tempo Real:** Cálculo dinâmico do subtotal da tigela conforme opções e quantidades são marcadas.
- [x] **Carrinho de Compras:** Ajuste de quantidades, remoção de itens e cálculo do total da sacola.
- [x] **Finalização e Integração WhatsApp:** Confirmação do pedido com envio dos detalhes prontos para o WhatsApp do estabelecimento.
- [x] **Meus Pedidos:** Acompanhamento do histórico e status de cada pedido (`Em Preparo`, `Entregue`, `Cancelado`).

### 🔑 Para Administradores
- [x] Painel de Dashboard com métricas de pedidos e vendas.
- [x] Gerenciamento de tamanhos de açaí e adicionais.
- [x] Atualização de status dos pedidos em preparo ou entregues.

---

## 🎨 Design e Identidade Visual

* **Paleta de Cores:**
  - 💜 **Roxo Açaí Profundo:** Gradientes modernos (`#120224`, `#1e0638`, `#8b5cf6`)
  - 💚 **Verde Folha Fresca:** Destaques e confirmações de compra (`#22c55e`, `#16a34a`)
  - 🤍 **Branco e Vidro Fosco:** Efeitos translúcidos de glassmorphism com tipografia nítida
* **Tipografia:** Google Fonts ([Outfit](https://fonts.google.com/specimen/Outfit) para títulos marcantes e [Poppins](https://fonts.google.com/specimen/Poppins) para leitura limpa)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java JDK 21 instalado no computador.

### Executando no Windows (Modo Rápido)
Dê um duplo clique no script:
```text
iniciar.bat
```
Ele iniciará o servidor Javalin e abrirá seu navegador automaticamente em `http://localhost:7070`.

### Executando via Terminal
```bash
# 1. Navegue até a pasta do projeto
cd StackBerry_Sistema_De_Gerenciamento_Para_Acaiterias

# 2. Execute a aplicação empacotada
java -jar target/s_berry-1.0-jar-with-dependencies.jar

# 3. Acesse no navegador:
http://localhost:7070
```

---

## 👩‍💻 Autoria

* **Desenvolvimento e Refatoração Visual:** Anna Beatriz Alves de Vasconcelos ([@Annabdv](https://github.com/Annabdv))
* **Projeto desenvolvido no contexto acadêmico:** UFPB - Campus IV (Rio Tinto / Mamanguape)
