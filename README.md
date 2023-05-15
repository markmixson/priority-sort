# Priority Sorting using Redis Sorted Sets

This tool takes a prioritized list of binary values and arranges them in a sorted set in Redis.  

How to use:
1. Give the BitSetGenerator an array of integers representing rule matches of length N.  0 is the lowest rule match, N - 1 is the highest. 
2. Add the BitSet and the UTC ZonedDateTime/id to a RuleMatchResult.
3. Use the RedisPrioritySortMutationClient to add/update/delete the result in Redis.
4. Use the RedisPrioritySortQueryClient to query the top priority (or RuleMatchResult) of the sorting.