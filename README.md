# Aramari RUSH - Diamonds for the Queen

Um jogo de aventura 2D onde você precisa coletar diamantes enquanto enfrenta inimigos para ajudar a Rainha. Orientados por professora Larissa Rocha.

Desenvolvevores: Emanuel Gomes Bispo e Marco Antonio Bomfim Cardoso Dantas

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven (para compilar)

## 🚀 Como executar

### Usando Maven

1. Clone o repositório:
```bash
git clone https://github.com/EnueLLL1/Aramari-RUSH.git
cd Aramari-RUSH
```

2. Compile o projeto:
```bash
mvn clean package
```

3. Execute o jogo:
```bash
java -jar target/aramari-rush-1.0-SNAPSHOT.jar
```

### Usando sua IDE favorita

#### IntelliJ IDEA
1. Vá em `File -> Open`
2. Selecione a pasta do projeto
3. A IDE deve reconhecer automaticamente como um projeto Maven
4. Execute a classe `AramariRUSH.Container`

#### VS Code
1. Instale as extensões:
   - Extension Pack for Java
   - Maven for Java
2. Abra a pasta do projeto
3. Execute a classe `AramariRUSH.Container`

## 🎮 Controles
- Setas direcionais: Movimento
- Espaço: Atirar
- ESC: Pause/Menu

## 🛠️ Desenvolvido com
- Java
- Swing para interface gráfica
- Maven para gerenciamento de dependências

## 📁 Estrutura do Projeto
- `src/AramariRUSH`: Código principal do jogo
- `src/Modelo`: Lógica do jogo
- `src/Modelo/Entidades`: Classes das entidades do jogo
- `src/Modelo/UI`: Interface do usuário
- `src/res`: Recursos (mapas, sprites, etc)
- `src/tile`: Sistema de tiles do mapa

## 🤝 Contribuindo
Sinta-se à vontade para abrir issues ou enviar pull requests com melhorias.

## 📄 Licença
Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.