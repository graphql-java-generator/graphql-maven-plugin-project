# graphql-java-client
Makes it easy to develop GraphQL client, in java. This project uses the schema-first approach.

## What's to be done

### Request

Generate a request, like this:

```php
{
  hero(episode: NEWHOPE) {
    id
    name
    appearsIn
  }
}
```


### Response

Reads a response like the one below, and maps it into the relevant Java objects

```json
{
  "data": {
    "hero": {
      "id": "An id",
      "name": "A hero's name",
      "appearsIn": [
        "NEWHOPE",
        "JEDI"
      ]
    }
  }
}
```

