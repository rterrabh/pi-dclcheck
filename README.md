# pi-dclcheck
Platform-independent DCL checker

The idea is to verify architecture software systems written in any programming language.

Usage (get the last release in [dist] directory):
> pi-dclcheck [dcl-file] [folder-dir] [dependencies-file]

For example:
> java -jar pi-dclcheck architecture.dcl /home/fooproject dependencies.txt

It creates the *violations.txt* file reporting the found divergences and absences.

Prerequisite: In the target project directory, there must be a file with all dependencies in which **each** line is as follows:
> [source-class-full-qualified-name] , [dependency-type] , [target-class-full-qualified-name]

For Java, see project javadepextractor at https://github.com/rterrabh/javadepextractor
