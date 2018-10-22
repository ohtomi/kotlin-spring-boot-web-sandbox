# kotlin-spring-boot-web-sandbox

## how to run

### `Gradle`

```console
$ make genkeypair
...

$ make bootRun
...

$ open https://localhost:8443
```

### `Docker`

```console
$ make genkeypair
...

$ make jibDockerBuild
...

$ make jibRun
...

$ open https://localhost:8443
```

## Prerequisite

- `Java`
- `Gradle`
- `Docker` (optional for `Jib`)

## See Also

- [Spring Initializr](https://start.spring.io/#!language=kotlin)
- [Jib](https://github.com/GoogleContainerTools/jib)

## Contributing

1. Fork it!
1. Create your feature branch: `git checkout -b my-new-feature`
1. Commit your changes: `git commit -am 'Add some feature'`
1. Push to the branch: `git push origin my-new-feature`
1. Submit a pull request :D

## License

MIT

## Author

[Kenichi Ohtomi](https://github.com/ohtomi)
