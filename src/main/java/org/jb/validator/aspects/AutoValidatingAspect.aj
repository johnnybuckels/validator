
aspect AutoValidatingAspect {
    pointcut callingValidatedMethod(): (call())
}