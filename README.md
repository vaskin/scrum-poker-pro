# ScrumPokerPro

ScrumPokerPro is developing a poker planning and sprint retrospective service for distributed teams using agile methodologies in its work.
The Covid-19 pandemic has seriously changed the reality in which we live. A lot of companies have partially or completely switched to a remote work mode, which made it possible to gather teams with participants from different cities of the world and get rid of the need to meet in the office. But how to organize processes in teams that work by Scrum and used to spend a lot of time in the office together? The ScrumPokerPro service solves the problem of organizing ceremonies in such teams and allows them to be held online.

# What is the architecture of ScrumPokerPro

![architecture](architecture.svg)

# Cloud provider

We use Scaleway as our cloud provider. The main selection criteria were the availability of Kubernetes, PostgreSQL, S3, Container Registry managed services as well as the presence of a provider in Terraform for resource management. Yes, we follow the Infrastructure as Code approach. And of course the price.

# Workloads

We love about JVM and use Java 17 with Kotlin. Sprint Boot, Spring WebFlux and Koltin Coroutines allow us to implement reactive non-blocking approach.

scrum-poker-pro — managing meeting, profiles, groups and handling websocket connections: kotlin, coroutines, spring boot, sprint webflux, r2dbc, gradle

scrum-poker-pro-jira — integration with Jira Cloud: kotlin, spring boot, gradle

keycloak — open source identity and access management

scrum-poker-pro-landing — landing page: html, css, pug, sass, gulp. Available at the https://scrumpokerpro.com

scrum-poker-pro-frontend — main React web-application: html, css, javascript, react, webpack. Available at the https://space.scrumpokerpro.com

# Persistent storage

PostgreSQL is the main database of our architecture. We use a managed service from Scaleway cloud provider which deployed in a highly available configuration with 1 primary node and 1 standby node.

S3 Object Storage is a file storage that stores user avatars, privacy policy files and grafana dashboards.

# CI/CD

Gitlab CI implements continuous integration and we use GitOps. ArgoCD is a declarative GitOps continuous delivery tool for Kubernetes that allows us to deliver applications to Kubernetes and store all configurations in a git repository.

![cicd](cicd.svg)