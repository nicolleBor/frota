# 🚛 Sistema de Gestão de Frota - Frota

Sistema completo de gestão de frota com cálculo inteligente de frete baseado em cubagem e APIs de roteamento.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Como Usar](#como-usar)
- [API de Frete](#api-de-frete)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuição](#contribuição)

## 🎯 Sobre o Projeto

O **Sistema de Gestão de Frota** é uma aplicação web desenvolvida em Spring Boot que permite o gerenciamento completo de uma empresa de transporte rodoviário. O sistema implementa cálculos de frete inteligentes baseados em cubagem, utilizando APIs externas para cálculo de distância e pedágio.

### Características Principais

- ✅ **Cálculo de Frete Inteligente**: Sistema baseado em cubagem como o Mercado Livre
- ✅ **APIs de Roteamento**: Integração com OpenRouteService para distâncias reais
- ✅ **Gestão Completa**: Caminhões, produtos, caixas e solicitações de transporte
- ✅ **Interface Moderna**: Dashboard responsivo com Bootstrap
- ✅ **Validações de Negócio**: Produtos devem caber nas caixas selecionadas

## 🚀 Funcionalidades

### 📦 Gestão de Entidades
- **Caminhões**: Cadastro com dimensões (comprimento, largura, altura) e fator de cubagem
- **Produtos**: Gestão de produtos com peso e dimensões
- **Caixas**: Caixas padronizadas com material e limite de peso
- **Solicitações**: Solicitações de transporte com cálculo automático de frete

### 💰 Sistema de Cálculo de Frete

#### Método 1: Sistema de Faixas de Peso (Mercado Livre)
- **0-1kg**: R$ 12,00
- **1-5kg**: R$ 18,00
- **5-10kg**: R$ 25,00
- **10-30kg**: R$ 35,00
- **30kg+**: R$ 45,00

#### Método 2: Cálculo por Distância
- **Peso**: R$ 0,08/km
- **Volume**: R$ 0,10/km
- **Caixa**: R$ 0,09/km

#### Multiplicadores de Distância
- **0-50km**: 1.0x
- **51-200km**: 1.1x (+10%)
- **201-500km**: 1.2x (+20%)
- **501-1000km**: 1.3x (+30%)
- **1000km+**: 1.4x (+40%)

#### Taxas Fixas
- **Coleta**: R$ 5,00
- **Entrega**: R$ 8,00
- **Seguro**: R$ 2,00
- **Pedágio**: Calculado pela API

### 🧮 Lógica de Cubagem
- **Fator de Cubagem**: 300 kg/m³ (transporte rodoviário)
- **Peso Cubado**: Volume (m³) × Fator de Cubagem
- **Peso Cobrado**: Maior entre peso real e peso cubado
- **Método Final**: Maior valor entre os dois métodos de cálculo

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 18**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **Spring MVC**
- **MapStruct** (mapeamento de objetos)
- **Bean Validation**
- **Lombok**

### Frontend
- **Thymeleaf** (template engine)
- **Bootstrap 5** (UI framework)
- **JavaScript** (interatividade)

### Banco de Dados
- **MySQL 8.0**
- **Docker** (containerização)

### APIs Externas
- **OpenRouteService** (cálculo de distância e pedágio)

## 📋 Pré-requisitos

- **Java 18+**
- **Maven 3.6+**
- **Docker** (para MySQL)
- **Git**

## ⚙️ Instalação e Configuração

### 1. Clone o Repositório
```bash
git clone <url-do-repositorio>
cd frota
```

### 2. Configure o Banco de Dados
```bash
# Inicie o container MySQL
docker run --name frota-mysql \
  -e MYSQL_ROOT_PASSWORD=alunofatec \
  -e MYSQL_DATABASE=frota \
  -p 3307:3306 \
  -d mysql:8.0.16
```

### 3. Configure a API Key
Edite o arquivo `src/main/resources/application.properties`:
```properties
# Substitua pela sua chave da OpenRouteService
frete.api.openrouteservice.key=SUA_CHAVE_AQUI
```

### 4. Execute a Aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8083`

## 🎮 Como Usar

### 1. Acesse o Dashboard
- URL: `http://localhost:8083`
- Visualize estatísticas gerais da frota

### 2. Cadastre as Entidades
1. **Caminhões**: Defina dimensões e características
2. **Produtos**: Cadastre produtos com peso e dimensões
3. **Caixas**: Configure caixas padronizadas

### 3. Crie Solicitações de Transporte
1. Selecione caminhão, produto e caixa
2. Informe origem e destino
3. Clique em "Calcular Frete" para simular
4. Clique em "Confirmar Solicitação" para salvar

### 4. Visualize Resultados
- Dashboard com estatísticas
- Listagem de solicitações
- Detalhes do cálculo de frete

## 🌐 API de Frete

### Endpoint de Cálculo
```
GET /solicitacao/calcular-frete
```

**Parâmetros:**
- `origem`: Cidade de origem (ex: "São Paulo,SP")
- `destino`: Cidade de destino (ex: "Rio de Janeiro,RJ")
- `produtoId`: ID do produto
- `caminhaoId`: ID do caminhão (opcional)
- `caixaId`: ID da caixa (opcional)

**Resposta:**
```json
{
  "distanciaKm": 360.75,
  "valorTotal": 78.96,
  "pesoCobrado": 0.2,
  "pesoCubado": 0.038,
  "metodoUsado": "distancia",
  "valorFretePeso": 15.12,
  "valorFreteDistancia": 38.96,
  "taxaColeta": 5.0,
  "taxaEntrega": 8.0,
  "taxaSeguro": 2.0,
  "pedagio": 25.0
}
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/example/frota/
│   │   ├── api/externo/          # Integração com APIs externas
│   │   ├── caminhao/             # Gestão de caminhões
│   │   ├── caixa/                # Gestão de caixas
│   │   ├── produto/              # Gestão de produtos
│   │   ├── solicitacao/          # Solicitações de transporte
│   │   ├── home/                 # Dashboard
│   │   └── FrotaApplication.java # Classe principal
│   └── resources/
│       ├── templates/            # Templates Thymeleaf
│       ├── static/               # Arquivos estáticos
│       └── application.properties # Configurações
└── test/                         # Testes unitários
```

### Principais Componentes

- **FreteService**: Cálculo de frete com APIs externas
- **ValidacaoService**: Validações de negócio
- **Controllers**: Endpoints REST e páginas web
- **Services**: Lógica de negócio
- **Repositories**: Acesso a dados
- **DTOs**: Transferência de dados
- **Mappers**: Conversão entre entidades e DTOs

## 🔧 Configurações Avançadas

### Variáveis de Ambiente
```properties
# Banco de Dados
spring.datasource.url=jdbc:mysql://localhost:3307/frota
spring.datasource.username=root
spring.datasource.password=alunofatec

# Servidor
server.port=8083

# API Externa
frete.api.openrouteservice.key=SUA_CHAVE
```

### Logs
Para habilitar logs detalhados, descomente em `application.properties`:
```properties
logging.level.com.example.frota=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 🧪 Testando o Sistema

### Cenários de Teste Recomendados

1. **Produto Leve (Smartphone)**
   - Peso: 0.2kg
   - Volume: 0.000128 m³
   - Peso cubado: 0.038kg
   - Peso cobrado: 0.2kg (peso real)

2. **Produto Volumoso (Notebook)**
   - Peso: 2.5kg
   - Volume: 0.002 m³
   - Peso cubado: 0.6kg
   - Peso cobrado: 2.5kg (peso real)

3. **Produto Muito Leve e Volumoso**
   - Peso: 0.1kg
   - Volume: 0.01 m³
   - Peso cubado: 3.0kg
   - Peso cobrado: 3.0kg (peso cubado)

### Rotas de Teste
- **São Paulo → Rio de Janeiro**: ~430km
- **São Paulo → Recife**: ~2600km
- **Rio → Belo Horizonte**: ~440km

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Autores

- **Desenvolvido por**: Nicolle Borges
- **Contato**: nicolleborges3645@outlook.com

## 🙏 Agradecimentos

- OpenRouteService pela API de roteamento
- Spring Boot pela excelente framework
- Bootstrap pela interface moderna
- Comunidade Java pelo suporte

---

**📧 Dúvidas? Entre em contato!**

**🚀 Happy Coding!**
