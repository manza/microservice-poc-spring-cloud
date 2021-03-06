Microservices

-> Config Server (Implementado --> Github ms-config-server, ms-configurations)
  * todas as configura��es da App em um servico distribu�do. 
  * Esse servi�o � baseado em um reposit�rio GIT

  * Feito atrav�s do Spring Cloud Config

-> Actuator Refresh
  * Ao atualizar property values no config server (repo). Os novos valores n�o s�o expostos automaticamente.
    � necess�rio que o client fa�a:
    - Chamada POST <service>/actuator/refresh para obter a lista de propriedades alteradas . 
      (Invi�vel pois pode haver centenas de inst�ncias do servi�o up/running)
    - Chamda GET normal para obter os valores mais atualizados

    - Alternativa � Spring Cloud Bus, onde voc� tem uma �nica URL para cada microservice e quando solicitada, � feita
      a atualiza��o do valor mais recente em todas as inst�ncias up/running
      Passos:
      a) Adiciona dependencia no projeto pom
      b) Tem um RabbitMQ em execu��o
      c) Automaticamente integrado
      d) Faz chamada POST para /bus/refresh
      e) Automaticamente atualiza todas as inst�ncias

-> Service Registry/Discovery (Naming Server)
  * Gerenciar todos os host/ports onde cada servi�o se encontra
  * Cada microservice se registra no Service Registry para dizer que est� dispon�vel
  * Tamb�m realiza checagens de health checks e balanceamento de carga
  * Dessa maneira n�o precisamos ter properties que geram acoplamento entre servi�os dependentes (microservice A -> microservice B)
    Al�m disso, elemina o problema quando microservi�os escalam. 
  * � poss�vel criar um cluster de Service Discoveries

  * Feito atrav�s do Eureka Netflix

-> Spring Cloud Feign 
  * Very simple tool to invoke other microservices
  * Client for Restful services in replacement of RestTemplate
  * Provides integration with Ribbon for client side load balancing

-> Ribbon (Load Balance)
  * Enables the clients to perform calls to provider APIs independently of unique host.
    It makes possible that the client perform calls to any provider API instance available distributing the load by
    only the active instances of the provider by following "round-robin" strategy.

  * Steps
    a) add dependency
    b) enable ribbon client in the Feign Proxy class
    c) configure the property <app.name>.riboon.listOfServers

  * At first service call, it is logged the Ribbon configuration and shows stats about the provider instances

-> Circuit Breaker (Hystrix / Fault Tolerance)
  * Gerencia toda quest�o falhas de chamadas para que n�o fique realizando recurrente chamadas para
    um microservice fornecedor. Se todos clientes tem erros ao consumir um microservico indisponivel
    isso pode acarretar em uma falha cr�tica de cascata no sistema todo.
  * Chamadas que podem falhar s�o tratadas pelo Circuit Breaker tendo uma:  
    - tratativa de erro
    - alimentando as m�tricas de monitoramento do sistema para informar que determinado servico est� fora
  * Feito atrav�s do Hystrix Netflix

-> Gateway
  * Determina um ponto de entrada �nico para todos os clientes dos microservices. (Ao inv�s de fazer acesso 
    direto as host/portas do microservico
  * Todo o trafego passa pelo Gateway e ent�o � encaminhada para os microservices respeitando as rotas
    configuradas
  * Cuida tamb�m de gerenciamento de problemas, se determinado micro-service est� down (todas as instancias),
    faz o registro da falha e prove uma resposta default
  * Service agreggation: se determinado client consome 15 diferentes micro-services, � poss�vel agregar todos
    os 15 servicos em um �nico, ent�o que o consumer "pense" que est� chamando um �nico servi�o.
  * Fluxo:
    a) Gateway recebe o request
    b) Consulta no Service Registry qual � a instancia e endere�o/porta do microservice respons�vel
    c) Se for uma rota segura ent�o realiza a autenticacao junto ao servidor de Autorizacao e segue.

    Format URI

    http://<gateway-host>:<gateway:port>/<microservice-app-name>/<microservice-app-uri>
    Ex: http://localhost:8765/currency-exchange-service/currency-exchange/from/{from}/to/{to}

  * Feito atrav�s do Zuul Netflix

-> Distributed Tracing
   * Um �nico lugar onde voc� ter informa��es end-to-end sobre um determinado request,
     ent�o que voc� seja capaz de identificar problemas. Ao inv�s de ir investigando,
     cada micro-service existente.

   * Sleuth anexo um Identificador �nico para cada request para facilitar todo o tracing 
     por todo o ciclo de vida do request (entre todos os micro-services).

   * Feito atrav�s da biblioteca Spring Cloud Sleuth / Zipkin

-> Zipkin (Tracing requests)
  * Tool respons�vel por centralizar todos os logs do ecosistema em �nico lugar para que podemos
    consultar e ser capazes de "debugar" cada request especificamente.
  * Zipkin prov� uma UI para a visualiza��o e gerenciamento dos logs.
  * Necess�rio RabbitMQ
      - Micro-services postam todo o tracing para o RabbitMQ via Sleuth
      - Zipkin busca informa��es do RabbitMQ
      - Todo o tracing fica dispon�vel via Zipkin UI
  * Alternativas: Elastic Search, Logstash, Kibana



