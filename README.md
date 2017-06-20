# pi-dclcheck
Platform-independent DCL checker

The idea is to verify architecture software systems written in any programming language.

> Command-line usage (get the last release in [dist] directory):
pi-dclcheck [dcl-file] [folder-dir] [dependencies-file]
```
For example:
java -jar pi-dclcheck architecture.dcl /home/fooproject dependencies.txt
```
It creates the *violations.txt* file reporting the found divergences and absences.

> API usage (get the last release in [dist] directory):
```
Add pidclcheck.jar in your lib
In pidclcheck.main.Main, invoke the following method:
Collection<ArchitecturalDrift> validateLocalArchitecture(InputStream dependenciesIn, InputStream constraintsIn)
It receives as input the `dependenciesIn` stream with all project dependencies (see format below) and the `constraintsIn` stream with the DCL constraints. It returns as output a list of ArchitecturalDrift.
```

Prerequisite: In the target project directory (or inside the dependenciesIn stream, in case of using throw the API), there must be a file with all dependencies in which **each** line is as follows:
> [source-class-full-qualified-name] , [dependency-type] , [target-class-full-qualified-name]

For Java, see project javadepextractor at https://github.com/rterrabh/javadepextractor
