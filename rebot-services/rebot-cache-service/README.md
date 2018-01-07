# ReBot Cache Service

This service provides support to Infinispan, only in memory cache, to be used across the API and any other service
that needs to interact with a cache.

There is some configured caches specific to a service or plugin, for example, the Karma cache, intended to be used
only by the ReBot Karma Plugin.

There is also a generic cache, that can be used like this example:

```java
@Inject
@KarmaCache
Cache<String, Integer> cache

public void someMethod() {}
    cache.pubIfAbsent("username", 100);
}
```

### Did you find a but or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email: just@rebase.it