# Elasticsearch

### ENV
- docker compose
- elasticsearch:8.10.3
- kibana:8.10.3
- java 17
- springboot 3.2.12
- spring-data-elasticsearch: 5.2.12


### TODO:
- 색인 request
- 색인 _bulk 처리 
- 검색 request 
- 색인 jdbc (optional) 
- Rest Error 처리
- RestControllerAdvice
- 가능하다면 자동 완성 + api request 인증 
- 


### Note 
- full text query : match 쿼리
- 전략 패턴 사용



> Nori 설치
> - Docker 컨테이너 접속
>  - docker exec -it `컨테이너명` bash
> - bin/elasticsearch-plugin install analysis-nori
> - 설치 후 docker-compose restart
