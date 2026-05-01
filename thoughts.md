- :: is for looking up specifically-versioned scala dependencies
extra colon appends version for lookup
```bash
//> using dep "com.thesamet.scalapb::scalapb-runtime-grpc:0.11.20"
```

- need research on this lib, suggested by AI
```bash
// JODConverter for LibreOffice
//> using dep "org.jodconverter:jodconverter-local:4.4.11" 
```

- Also some research into this one | has .debug
```bash
// logs
//> using dep "org.slf4j:slf4j-simple:2.0.17"
```
another logging thing logback but looks complicated, look into later

put in dockerfile to give to people, maybe missing something i already have


a few problems, mostly handling the ports to give to the pool, if I try to give an instance count variable to the .env then I have to manage more stuff,
I will keep it to just one instance, one port 8009, which is just one above.

current state is alright, simple conversion excel to pdf works. is synchronous rn, if I add async then I'd still need to add an "giver" of requests to the
collection of instances, but I think those as managed internally by JOD from portNumbers.
Dockerfile still on todo list


Dockerfile made now, not sure if to use compose and then have libre office in it's own container maybe, might just be overcomplicating it if no changes need to happen to it and the JOD works on it too

I have a build.sh now just for iteration, tested it, it still works inside
it probably needs some volume or so if it wants to handle files in a given space and interact with other things, containerization is a bit infectious
will do some other day, not important

project is fine right now, simple dependencies, install and structure.

to make this cool might try optimizations, try to port the current logic to scala native, or rewrite to a different language,
async stuff might be done, keeping a hashtable with stuff so as to not process identical files multiple times, testing harness needs to be made
lots of stuff still to do
