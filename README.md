# Blog do Agi - Automação de testes

Este repositório contém uma suíte de testes automatizados desenvolvidos em Java usando Selenium WebDriver e JUnit para validar a funcionalidade de pesquisa no site Blog do Agi. O projeto inclui testes de funcionalidade e segurança, como SQL Injection.

## Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior
- Google Chrome
- WebDriverManager

## Configuração do Ambiente

1. **Clonar o repositório:**

```bash
  git clone https://github.com/RBoker/agi-test.git
```
2. **Instalar as dependências:**

O Maven é utilizado para gerenciar as dependências do projeto, que estão listadas no arquivo pom.xml. Execute o seguinte comando para instalar as dependências:
  
```bash
   mvn clean install
``` 
3. **Executar os testes:**

Para executar todos os testes, utilize o comando:
```bash
   mvn test
```
## Estrutura do Projeto
src/test/java/br/com/rboker/blogdoagi/search/: Contém as classes de teste.
SearchTests.java: Testes de funcionalidade que seguem as melhores práticas de programação.
SqlInjectionTests.java: Teste de segurança que simula um ataque de SQL Injection para verificar vulnerabilidades.

## Detalhes dos Testes
1. `SearchTests.java`

- Verifica se a pesquisa por um termo existente retorna resultados relevantes.
- Verifica se a pesquisa por um termo inexistente retorna a mensagem "nenhum resultado encontrado".

2. `SqlInjectionTests.java`

- Verifica se o campo de pesquisa do Blog do Agi é vulnerável a ataques de SQL Injection.
- Atenção: Este teste deve ser executado apenas em ambientes de teste controlados.


3. Um relatório simplificado da execução pode ser visto em:
````bash
target/site/surefire-report.html
````

## Contribuição
Contribuições são bem-vindas! Se você quiser contribuir com este projeto, siga estas etapas:

1. Faça um fork do repositório.

2. Crie uma nova branch com uma descrição significativa:

````bash
git checkout -b minha-feature
````
3. Faça as alterações necessárias e adicione testes para elas.

4. Envie suas alterações:

````bash
git push origin minha-feature
````
5. Abra um Pull Request.

## Licença
Este projeto é licenciado sob a licença MIT. Consulte o arquivo LICENSE para obter mais detalhes.



## Contato
Para sugestões ou duvidas, contate:

Nome: Davi Mares 

Email: davimares1@gmail.com

LinkedIn: https://www.linkedin.com/in/davi-mares/


### Explicação do README

Este README fornece um guia completo para entender, configurar e contribuir com o projeto. Ele inclui:

Introdução: Uma visão geral do propósito do repositório.
Requisitos: Especifica as dependências necessárias para executar o projeto.
Configuração do Ambiente: Instruções passo a passo para configurar o ambiente de desenvolvimento.
Estrutura do Projeto: Uma descrição da estrutura do código para ajudar novos colaboradores.
Detalhes dos Testes: Explicações sobre os principais testes e a importância do teste de SQL Injection.
Contribuição: Diretrizes para quem deseja contribuir com o projeto.
Licença: Informações sobre a licença do projeto.
Contato: Informações de contato do autor do projeto.
