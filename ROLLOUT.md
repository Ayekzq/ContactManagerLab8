# Release Rollout Strategy

## Application
ContactManager

## Staged Rollout

| Stage | Audience | Monitoring |
|---|---:|---|
| 1 | 1% | 24 hours |
| 2 | 10% | 24 hours |
| 3 | 50% | 48 hours |
| 4 | 100% | Final release |

## Release Strategies

- Staged Rollout: gradual distribution.
- Closed Testing: distribution to selected testers.
- Full Release: distribution to all eligible users.

## Stop Conditions

- Crash rate exceeds 1%.
- ANR rate exceeds 0.5%.
- Crash or ANR rate doubles against baseline.
- Confirmed contact data loss.
- Critical application functionality failure.
- Significant decrease in user rating.

## GitHub Release Process

1. Build signed APK and AAB.
2. Create GitHub Pre-release.
3. Review build and test results.
4. Require manual production approval.
5. Publish stable GitHub Release.

Note:
GitHub Releases does not support percentage-based
user rollout. Staged rollout percentages describe
the planned Google Play distribution strategy.