# Mahjong game keeper

These is the service for calculating mahjong games scores and keeping track of the games.
Service based on modular microservice architecture.

## Modules

### mahjong-security
Provides API for authentication for users. Every microservice should use it if it requires
to reduce anonymous access to its endpoints.

Backend service located in mahjon-security module and API is separated into mahjong-security-api
module. Other services have to depend on mahjong-security-api if they require authentication.

Service requires Postgres database and Java to run. It can be packed into debian package with
`buildDeb` gradle target (first have to build service itself with `build` target). To build service
you will require running postgres database to generate data base schema classes. You can find
connection details in th build.gradle file.

### mahjong-main
Provides common functionality to start game, update it results, maintain players and wil provide 
common statistics in the future.

Backend service located in mahjong-main module. Also module provides to API:
  * mahjon-main-public-api provide common UI functions for getting games and players
  information and also control game's flow
  * mahjong-main-private-api provide functions required for other services to extend
  functionality of the platform, like start new game, get more detailed players information,
  batch game methods.
  
Service requires Postgres database and Java to run. It can be packed into debian package with
`buildDeb` gradle target (first have to build service itself with `build` target). To build service
you will require running postgres database to generate data base schema classes. You can find
connection details in th build.gradle file.

### mahjong-league
League is a long time tournament without any predefined seatings. League's players just
playing games between each other and earn rating inside league. Everyone can create 
its own league and invite any player, also any league player can invite any other player.
Any player can send join request to the league and league's admin can approve it.

Backend service located in mahjon-league module. It provides API for creating and modifying 
leagues itself, managing players and starting games inside league.

Service requires Postgres database and Java to run. It can be packed into debian package with
`buildDeb` gradle target (first have to build service itself with `build` target). To build service
you will require running postgres database to generate data base schema classes. You can find
connection details in th build.gradle file.

### mahjong-view
Angular based UI uniting all backend service into single application.

Service requires Postgres database and Java to run. It can be packed into debian package with
`buildDeb` gradle target. It will require node and npm installed on your machine.