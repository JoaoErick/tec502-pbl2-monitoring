# tec502-pbl2-monitoring

<p align="center">
  <img src="https://i.imgur.com/hs6G3TG.png" alt="monitoring icon" width="650px" height="450px">
</p>

------------

## 📚 Descrição ##
**Resolução do problema 2 do MI - Concorrência e Conectividade (TEC 502).**<br/><br/>
O projeto trata-se de um dos *clients* que se conecta ao [servidor](https://github.com/AllanCapistrano/tec502-pbl2-server) principal, e tem como função exibir os dados, que são medidos pelos sensores e processados no servidor, para os médicos poderem acompanhar a situação em tempo real dos pacientes que estão utilizando o dispositivo de monitoramento de COVID-19.<br/>
Este *client* se comunica com o servidor utilizando um protocolo baseado em [API REST](https://www.redhat.com/pt-br/topics/api/what-is-a-rest-api), com requisições [HTTP](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Methods) e mensagens no formato [JSON](https://www.json.org/json-en.html).

### ⛵ Navegação pelos projetos: ###
- [Servidor](https://github.com/AllanCapistrano/tec502-pbl2-server)
- [Fog](https://github.com/AllanCapistrano/tec502-pbl2-fog)
- [Emulador de Sensores](https://github.com/JoaoErick/tec502-pbl2-emulator)
- \>Monitoramento de Pacientes

### 🔗 Tecnologias utilizadas: ### 
- [Java JDK 8](https://www.oracle.com/br/java/technologies/javase/javase-jdk8-downloads.html)
- [Scene Builder](https://gluonhq.com/products/scene-builder/)

### 📊 Dependências: ### 
- [JSON](https://www.json.org/json-en.html)

------------

## 🖥️ Como utilizar ##
1. Para o utilizar este projeto é necessário ter instalado o JDK 8u111.

- [JDK 8u111 com Netbeans 8.2](https://www.oracle.com/technetwork/java/javase/downloads/jdk-netbeans-jsp-3413139-esa.html)
- [JDK 8u111](https://www.oracle.com/br/java/technologies/javase/javase8-archive-downloads.html)

2. Após instalado, basta executar o projeto na sua IDE de escolha.

### Através de arquivos já gerados ###
1. Caso esteja utilizando o sistema operacional **Windows**, [clique aqui]() e faça o download do arquivo `.exe` ou `.jar`;
2. Porém, caso esteja utilizando o sistema operacional **macOS** ou alguma distribuição **Linux**, [clique aqui]() e faça o download do arquivo `.jar`;
3. Após isso, com o [servidor](https://github.com/AllanCapistrano/tec502-pbl2-server) *online*, basta instalar o programa através do arquivo `.exe` ou executar o arquivo `.jar`.

###### Obs1: Utilizando o arquivo `.exe` não é necessário ter o JDK 8u111 instalado. ######
###### Obs2: Para executar o arquivo `.jar` é necessário ter o JDK 8u111 instalado. ######

------------

## 📌 Autores ##
- Allan Capistrano: [Github](https://github.com/AllanCapistrano) - [Linkedin](https://www.linkedin.com/in/allancapistrano/) - [E-mail](https://mail.google.com/mail/u/0/?view=cm&fs=1&tf=1&source=mailto&to=asantos@ecomp.uefs.br)
- João Erick Barbosa: [Github](https://github.com/JoaoErick) - [Linkedin](https://www.linkedin.com/in/joão-erick-barbosa-9050801b0/) - [E-mail](https://mail.google.com/mail/u/0/?view=cm&fs=1&tf=1&source=mailto&to=jsilva@ecomp.uefs.br)

------------

## ⚖️ Licença ##
[MIT License (MIT)](./LICENSE)
