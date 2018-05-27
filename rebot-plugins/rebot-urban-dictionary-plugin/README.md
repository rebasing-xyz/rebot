# ReBot Urban Dictionary Service/Client

Command that searches in the [Urban Dictionary](https://www.urbandictionary.com/) a english slang, for example


```
/urban lol
```

Will return something like this:

```
Definition: The name 'Lol' is an abreviated form of the name '[Laurence]'.

http://lol.urbanup.com/3689813
```

If the result is something that you are not expecting, you can increase the number of results:

```
/urban -c 2 lol
``` 

Also, it is possible to retrieve a usage example:
```
/urban -e lol
```

# Urban Dictionary Java Client
Besides the function of a command of ReBot it is also a client for Java Applications that can be embedded within your Java applications


### How to use it?
Add the following dependency on your pom.xml:

```java
<!-- Urban Dictionary client -->
<dependency>
    <groupId>it.rebase</groupId>
    <artifactId>rebot-urban-dictionary-service</artifactId>
    <version>0.1.Final</version>
</dependency>
```

Then

```java
UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").numberOfResults(1).showExample().build();
List<CustomTermResponse> response = client.execute();
```

Where:
- **term** is the word that you want to search.
- **numberOfResults** in it absence the default value is 1, you can add more results to your search by setting this parameter.
- **showExample** by default it is false, if you set it the result will bring a exemple about the term usage.

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email: just@rebase.it