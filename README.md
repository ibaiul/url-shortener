# URL Shortener application

This is a simple URL shortener application implemented using Java 11, SpringBoot and other nice technologies.

I developed this small project as part of my continuous learning process.

The main goals are to rehearse my TDD skills and to integrate DevOps and SiteReliability technologies into my personal cloud.

## Features

#### CRUD
- Create short URLs from long and hard to remember URLs
- Request DTO representations or the short URLs
- Update not available as it does not fit the business requirements
- Delete short URLs so that no one can access them anymore

#### Redirection
- Make a request to the short URLs to get redirected to the initial long URLs

#### Examples

```
# Shorten a URL and check Location header for the created resource URL
> curl -v --request POST --data '{"url":"https://www.google.com/search?q=TDD+Rocks%21%21"}' --header "Content-Type: application/json" localhost:8080/urls

HTTP/1.1 201
Location: http://localhost:8080/urls/53cf09da-5b02-4982-bd45-f3a0eaffb4f9


# Get one short URL resource
> curl --request GET localhost:8080/urls/{UUID}

{
 "id": "53cf09da-5b02-4982-bd45-f3a0eaffb4f9",
 "key": "h3ql",
 "url": "https://www.google.com/search?q=TDD+Rocks%21%21",
 "createdOn": "2020-03-13T15:08:07.942+0000"
}

 
# Get all short URL resources
> curl --request GET localhost:8080/urls

[
 {
   "id": "53cf09da-5b02-4982-bd45-f3a0eaffb4f9",
   "key": "h3ql",
   "url": "https://www.google.com/search?q=TDD+Rocks%21%21",
   "createdOn": "2020-03-13T15:08:07.942+0000"
 }
]


# Get redirected to long URLs
> curl -v --request GET localhost:8080/go/{key}

HTTP/1.1 301
Location: https://www.google.com/search?q=TDD+Rocks%21%21

> curl -v --request GET --location localhost:8080/go/{key}

HTTP/1.1 200


# Delete a short URL resource
> curl --request DELETE localhost:8080/urls/{UUID}

HTTP/1.1 204
```

## Tools / Methodologies

#### Java 11
Latest LTS java version at the time of writing.

#### TDD
The project has been implemented using a TDD approach from scratch.

#### REST
Simple REST API as an excuse to allow testing other technologies.

HTTP/2 enabled when using SSL/TLS

#### Spring Boot
I used this technology to accelerate the development of the application.

#### Swagger
To help understanding the API and its constraints.

Helps other backend and frontend developers at the time of integrating with the API. Normally is only activated in development and testing environments.

#### Metrics
With Spring Boot Actuator and Micrometer application/business metrics are exposed which get ingested by Prometheus in this case.

#### QA
Unit, integration and acceptance tests covering %100 of the code and ensuring user flows works as expected.

Smoke tests send alerts in case of failure or availability issues

#### Docker
While developing I use docker containers to isolate and easily reproduce production environments. The images I using include: MySQL, Prometheus, Grafana, etc.

In production those services run as system services to minimize the impact in the runtime performance.

#### CI/CD
The application is packed in a docker container and deployed using Jenkins pipelines.

#### Monitoring
Prometheus collects application metrics which I visualize using Grafana.

#### Security considerations (basic)
Public APIs are accessed through a proxy that enforces SSL/TLS (Letsencrypt) and offloads it in the backend

Internal network is connected through a VPN using WireGuard

Firewall rules and basic authentication are used to restrict access to critical paths through the internal network

Secrets are injected as part of the continuous delivery process, in this case using Jenkins instead of a vault to simplify things.

## TODO

#### Snyk
Integrate Snyk to check known vulnerabilities on the dependencies.

#### Logs
Send logs to my Logstash instances so that they are indexed in Elasticsearch and can be visualized in Kibana.

#### Docker
I am using the official openjdk-11 image which is not very slim.

I need to pack the application in a more lightweight image with openjdk-11 and alpine linux.

Use Docker Swarm or Kubernetes to deploy service, load balancing, circuit breaking, ...

#### QA
Integrate Checkstyle, SpotBugs, PMD and SonarQube as part of the CI/CD flow

Integrate Performance, Load, Stress and Chaos tests as part of the CI/CD flow (Gatlin, Chaos Monkey)
