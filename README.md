# onebot-springboot-adapter
A OneBot v11 protocol backend implemented in Java with Spring Boot.

# Implemented Features

- Identify and route user commands
- Authorization check

# Version

OpenJDK 17

Spring Boot 3.5.5

# Project Structure

```
│   BotApplication.java
│
├───authorization
│   ├───annotation
│   │       AuthInterceptor.java
│   │       AuthInterceptors.java
│   │
│   ├───aop
│   │       AuthorizationAspect.java
│   │
│   └───model
│           AuthMode.java
│           AuthScope.java
│
├───common
│   └───exception
│       │   BusinessException.java
│       │   CommandNotFoundException.java
│       │   ErrorCode.java
│       │   InvalidCommandArgsException.java
│       │   PermissionDeniedException.java
│       │   SessionNotExistsException.java
│       │
│       └───aop
│               BusinessExceptionHandlerAspect.java
│               GlobalRuntimeLoggingAspect.java
│
├───config
│       WebSocketConfig.java
│
├───controller
│       OneBotWsController.java
│
├───demos
│   └───web
│           BasicController.java
│           PathVariableController.java
│           User.java
│
├───dto
│   │   OneBotReturnActionDTO.java
│   │   ParsedPayloadDTO.java
│   │
│   └───segment
│           AtSegment.java
│           ImageSegment.java
│           MessageSegment.java
│           TextSegment.java
│
├───mapper
└───service
    │   ActionBuildService.java
    │   GroupCommandService.java
    │   ParseAndRouteService.java
    │   PrivateCommandService.java
    │
    ├───impl
    │       ActionBuildServiceImpl.java
    │       GroupCommandServiceImpl.java
    │       ParseAndRouteServiceImpl.java
    │       PrivateCommandServiceImpl.java
    │
    └───session
            SessionRegistry.java
```

