Microservices

-> Config Server (Implementado --> Github ms-config-server, ms-configurations)
  * todas as configurações da App em um servico distribuído. 
  * Esse serviço é baseado em um repositório GIT

  * Feito através do Spring Cloud Config

-> Actuator Refresh
  * Ao atualizar property values no config server (repo). Os novos valores não são expostos automaticamente.
    É necessário que o client faça:
    - Chamada POST <service>/actuator/refresh para obter a lista de propriedades alteradas . 
      (Inviável pois pode haver centenas de instâncias do serviço up/running)
    - Chamda GET normal para obter os valores mais atualizados

    - Alternativa é Spring Cloud Bus, onde você tem uma única URL para cada microservice e quando solicitada, é feita
      a atualização do valor mais recente em todas as instâncias up/running
      Passos:
      a) Adiciona dependencia no projeto pom
      b) Tem um RabbitMQ em execução
      c) Automaticamente integrado
      d) Faz chamada POST para /bus/refresh
      e) Automaticamente atualiza todas as instâncias

-> Service Registry/Discovery (Naming Server)
  * Gerenciar todos os host/ports onde cada serviço se encontra
  * Cada microservice se registra no Service Registry para dizer que está disponível
  * Também realiza checagens de health checks e balanceamento de carga
  * Dessa maneira não precisamos ter properties que geram acoplamento entre serviços dependentes (microservice A -> microservice B)
    Além disso, elemina o problema quando microserviços escalam. 
  * É possível criar um cluster de Service Discoveries

  * Feito através do Eureka Netflix

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
  * Gerencia toda questão falhas de chamadas para que não fique realizando recurrente chamadas para
    um microservice fornecedor. Se todos clientes tem erros ao consumir um microservico indisponivel
    isso pode acarretar em uma falha crítica de cascata no sistema todo.
  * Chamadas que podem falhar são tratadas pelo Circuit Breaker tendo uma:  
    - tratativa de erro
    - alimentando as métricas de monitoramento do sistema para informar que determinado servico está fora
  * Feito através do Hystrix Netflix

-> Gateway
  * Determina um ponto de entrada único para todos os clientes dos microservices. (Ao invés de fazer acesso 
    direto as host/portas do microservico
  * Todo o trafego passa pelo Gateway e então é encaminhada para os microservices respeitando as rotas
    configuradas
  * Cuida também de gerenciamento de problemas, se determinado micro-service está down (todas as instancias),
    faz o registro da falha e prove uma resposta default
  * Service agreggation: se determinado client consome 15 diferentes micro-services, é possível agregar todos
    os 15 servicos em um único, então que o consumer "pense" que está chamando um único serviço.
  * Fluxo:
    a) Gateway recebe o request
    b) Consulta no Service Registry qual é a instancia e endereço/porta do microservice responsável
    c) Se for uma rota segura então realiza a autenticacao junto ao servidor de Autorizacao e segue.

    Format URI

    http://<gateway-host>:<gateway:port>/<microservice-app-name>/<microservice-app-uri>
    Ex: http://localhost:8765/currency-exchange-service/currency-exchange/from/{from}/to/{to}

  * Feito através do Zuul Netflix

-> Distributed Tracing
   * Um único lugar onde você ter informações end-to-end sobre um determinado request,
     então que você seja capaz de identificar problemas. Ao invés de ir investigando,
     cada micro-service existente.

   * Sleuth anexo um Identificador único para cada request para facilitar todo o tracing 
     por todo o ciclo de vida do request (entre todos os micro-services).

   * Feito através da biblioteca Spring Cloud Sleuth / Zipkin

-> Zipkin (Tracing requests)
  * Tool responsável por centralizar todos os logs do ecosistema em único lugar para que podemos
    consultar e ser capazes de "debugar" cada request especificamente.
  * Zipkin provê uma UI para a visualização e gerenciamento dos logs.
  * Necessário RabbitMQ
      - Micro-services postam todo o tracing para o RabbitMQ via Sleuth
      - Zipkin busca informações do RabbitMQ
      - Todo o tracing fica disponível via Zipkin UI
  * Alternativas: Elastic Search, Logstash, Kibana



