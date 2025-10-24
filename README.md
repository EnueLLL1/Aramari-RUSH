# 💎 Diamonds for the Queen

<div align="center">

**Um jogo de aventura 2D onde você precisa coletar diamantes enquanto enfrenta inimigos para ajudar a Rainha!**

</div>

---

## 📖 Sobre o Projeto

Aramari RUSH é um jogo de aventura 2D desenvolvido em Java com interface gráfica Swing. O jogador embarca em uma missão para coletar diamantes preciosos enquanto enfrenta diversos desafios e inimigos ao longo do caminho, tudo isso para ajudar a Rainha em sua missão.

### 👥 Equipe de Desenvolvimento

- **Desenvolvedores:** Emanuel Gomes Bispo & Marco Antonio Bomfim Cardoso Dantas
- **Orientação:** Professora Larissa Rocha

---

## ✨ Características

- 🎮 Gameplay 2D clássico estilo arcade
- 💎 Sistema de coleta de diamantes
- 👾 Inimigos e obstáculos diversos
- 🗺️ Sistema de mapas baseado em tiles
- 🎨 Interface gráfica desenvolvida em Swing

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java
- **Interface Gráfica:** Swing
- **Build Tool:** Maven
- **Estrutura:** POO (Programação Orientada a Objetos)

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- Java 17 ou superior
- Maven 3.6 ou superior
- Uma IDE Java (IntelliJ IDEA, Eclipse ou VS Code recomendados)

---

## 🚀 Como Executar

### Método 1: Via Linha de Comando

1. **Clone o repositório:**
```bash
git clone https://github.com/EnueLLL1/Aramari-RUSH-Diamonds-for-the-Queen.git
cd Aramari-RUSH-Diamonds-for-the-Queen
```

2. **Compile o projeto:**
```bash
mvn clean package
```

3. **Execute o jogo:**
```bash
java -jar target/aramari-rush-1.0-SNAPSHOT.jar
```

### Método 2: IntelliJ IDEA

1. Abra o IntelliJ IDEA
2. Vá em `File → Open`
3. Selecione a pasta do projeto
4. A IDE reconhecerá automaticamente como projeto Maven
5. Execute a classe `AramariRUSH.Container`

### Método 3: Visual Studio Code

1. Instale as extensões necessárias:
   - Extension Pack for Java
   - Maven for Java
2. Abra a pasta do projeto
3. Execute a classe `AramariRUSH.Container`

---

## 🎮 Controles

|       Tecla    |          Ação           |
|----------------|-------------------------|
| **Setas WASD** | Movimento do personagem |
|    **Mouse**   |         Atirar          |

---

## 📁 Estrutura do Projeto

```
Aramari-RUSH/
│
├── src/
│   ├── AramariRUSH/       # Código principal do jogo
│   ├── Modelo/            # Lógica do jogo
│   │   ├── Entidades/     # Classes das entidades
│   │   └── UI/            # Interface do usuário
│   ├── res/               # Recursos (mapas, sprites, sons)
│   └── tile/              # Sistema de tiles do mapa
│
├── target/                # Arquivos compilados
├── pom.xml               # Configuração Maven
└── README.md             # Este arquivo
```

---

## 🎯 Como Jogar

1. Inicie o jogo seguindo as instruções de execução
2. Use as WASD para mover seu personagem
3. Colete os diamantes espalhados pelo mapa
4. Desvie ou elimine os inimigos usando o mouse

---

## 🤝 Contribuindo

Contribuições são sempre bem-vindas! Se você tem sugestões para melhorar o jogo:

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/NovaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a Branch (`git push origin feature/NovaFeature`)
5. Abra um Pull Request

Ou simplesmente abra uma **Issue** com suas sugestões e reportes de bugs.

---

## 📄 Licença
Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.