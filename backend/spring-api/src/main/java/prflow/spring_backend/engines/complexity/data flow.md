 


first config will store the initialize all coefficients of the formula and also 



## complexity service  

- It will get the data from db using jdbctemplate
#### Data it will calls 
- pullRequestState state
      ```data
  private record PullRequestState(
  Double complexityScore,
  String complexityLevel,
  LocalDateTime complexityCalculatedAt
  ) {}
```
   */
    private record SignalAggregate(
        int totalFilesChanged,
        int totalAdditions,
        int totalDeletions,
        int uniqueDirectoriesTouched
    ) {
        static SignalAggregate zero() {
            return new SignalAggregate(0, 0, 0, 0);
        }
    }
