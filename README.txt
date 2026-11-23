MATCH - Foundation starter files

Folder layout:
src/
  model/
    Program.java
    Trait.java
    Outcome.java
    ChoiceStatus.java
    Applicant.java
    RankedProgram.java
    ChoiceResult.java
    MatchResult.java
    ProgramProfile.java
  config/
    ProgramConfig.java

IMPORTANT PLEASE READ THANK YOU:
1. Keep the package declarations; put files in the matching folders.
2. These files use Java 8-compatible syntax. 
3. ProgramConfig section counts follow the group's current notes:
   BLIS=1, BSIS=1, BSEMC=1, BSCS=2, BSIT=2.
4. Numerical student capacity, score thresholds, and official trait-to-program
   mappings are placeholders and must be finalized by the group.
5. MatchingEngine should perform all calculations and sorting.
   RankedProgram and MatchResult store the results supplied by that engine.
6. If other members have already created classes with these names, compare signatures
   before replacing files. Please avoid duplicate classes in the same package.

Suggested compile command from the MATCH_Foundation folder:
javac -d out src/model/*.java src/config/*.java
