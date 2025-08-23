# onebot-springboot
A OneBot v11 protocol backend implemented in Java with Spring Boot.

# Implemented Features

- Identify and route user commands (as a WebSocket server)
- Authorization check
- Forward (as a WebSocket client, achieve by multi-strategy message queue)

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
│   ├───dto
│   │   │   OneBotReturnActionDTO.java
│   │   │   ParsedPayloadDTO.java
│   │   │
│   │   └───segment
│   │           AtSegment.java
│   │           ImageSegment.java
│   │           MessageSegment.java
│   │           TextSegment.java
│   │
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
│       ForwarderConfig.java
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
├───infrastructure
│   │   SessionRegistry.java
│   │
│   └───forwarder
│       │   ForwardDispatcher.java
│       │   ForwardMessageQueue.java
│       │   UpstreamPool.java
│       │
│       └───matcher
│           │   ForwardMatcher.java
│           │
│           └───impl
│                   DefaultForwardMatcher.java
│
├───mapper
├───service
│   ├───routing
│   │   │   GroupCommandRoutingService.java
│   │   │   ParseAndRouteService.java
│   │   │   PrivateCommandRoutingService.java
│   │   │
│   │   └───impl
│   │           GroupCommandRoutingServiceImpl.java
│   │           ParseAndRouteServiceImpl.java
│   │           PrivateCommandRoutingServiceImpl.java
│   │
│   └───test
└───utils
        ActionBuildUtil.java
        ForwardMatcherRegistry.java
```

