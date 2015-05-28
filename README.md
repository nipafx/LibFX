# LibFX

**[LibFX](http://libfx.codefx.org)** has no strict goal for a feature set. It collects functionality I created for my use in other projects but are abstract enough to be generally helpful. Many will revolve around JavaFX (hence the name).

This somewhat vague sentiment does not translate to quality! The code is clean, which especially includes thorough testing. It is also extensively documented and contains many examples. Bugs will be addressed quickly and feature requests as well as forks and pull requests are welcome.

## Features

These features are present in the latest release:

### JavaFX

* [ControlPropertyListener](https://github.com/CodeFX-org/LibFX/wiki/ControlPropertyListener): creating listeners for the property map of JavaFX controls
* [ListenerHandle](https://github.com/CodeFX-org/LibFX/wiki/ListenerHandle): encapsulating an observable and a listener for easier add/remove of the listener
* [Nestings](https://github.com/CodeFX-org/LibFX/wiki/Nestings): using all the power of JavaFX' properties for nested object aggregations
* [WebViewHyperlinkListener](https://github.com/CodeFX-org/LibFX/wiki/WebViewHyperlinkListener): add hyperlink listeners to JavaFX' `WebView`

### Collections

* [TransformingCollections](https://github.com/CodeFX-org/LibFX/wiki/TransformingCollections): transforming collections to a different parametric type
* [TreeStreams](https://github.com/CodeFX-org/LibFX/wiki/TreeStreams): streaming nodes of a graph

### Misc

* [SerializableOptional](https://github.com/CodeFX-org/LibFX/wiki/SerializableOptional): serializable wrapper for `Optional`


## Documentation

The best documentation are the tests. But understandably nobody wants to dig into them just to understand what the classes do so there are also examples and extensive Javadoc. The best way to get to know a feature is to check out the corresponding article in the [wiki](https://github.com/CodeFX-org/LibFX/wiki). It will include a high level explanation as well as links to demos and the best entry point into the documentation. The Javadoc of the current version is published [here](http://libfx.codefx.org/javadoc).

If anything is missing or an explanation proves to be unhelpful, contact me (see below).

## License

License details can be found in the *LICENSE* file in the project's root folder. The information provided there is binding but the gist is: **LibFX** is licensed under the GPL but if that does not suit your licensing model, other arrangements are possible (contact me; see below).

## Releases

Releases are published [on GitHub](https://github.com/CodeFX-org/LibFX/releases). The release notes also contain a link to the artifact in Maven Central and its coordinates.

The current version is [0.3.0](http://search.maven.org/#artifactdetails|org.codefx.libfx|LibFX|0.3.0|jar):

**Maven**:

``` XML
<dependency>
    <groupId>org.codefx.libfx</groupId>
    <artifactId>LibFX</artifactId>
    <version>0.3.0</version>
</dependency>
``` 

**Gradle**:

```
	compile 'org.codefx.libfx:LibFX:0.3.0'
```

## Development

Some information about how this library is developed:

* The [issue tracker](https://github.com/CodeFX-org/LibFX/issues) is actively used so it is the place to report bugs and request features.
* As per [GIT branching model](http://nvie.com/posts/a-successful-git-branching-model/) features are developed in feature branches. If you are curious, you can check out some branches to see what is being worked on.
* This is a [Maven](http://maven.apache.org/) project, so in case you want to check out the code, make sure your IDE knows about Maven.

## Infrastructure

The best starting point to everything regarding **LibFX** is [libfx.codefx.org](http://libfx.codefx.org).

The library has its home on [GitHub](https://github.com/CodeFX-org/LibFX) where the following features are especially noteworthy:
* the [issue tracker](https://github.com/CodeFX-org/LibFX/issues), which is actively used for development
* the [wiki](https://github.com/CodeFX-org/LibFX/wiki), where all features are introduced with their own article

I have a blog at [codefx.org](http://blog.codefx.org) where I might occasionally blog about **LibFX**. Those posts are filed under [their own tag](http://blog.codefx.org/tag/libfx/).

I use Eclipse and my project settings (like compiler warnings, formatter and save actions) can be found in the repository folder **.settings**. I know this is a little unusual but it makes it easier for contributors to cope with my obsession for warning free and consistently formatted code.

## Contact

Nicolai Parlog <br>
CodeFX

Web: http://codefx.org <br>
Twitter: https://twitter.com/nipafx<br>
Mail: nipa@codefx.org <br>
PGP-Key: http://keys.gnupg.net/pks/lookup?op=vindex&search=0xA47A795BA5BF8326 <br>
