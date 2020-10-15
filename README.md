
# Cron 

[![License](https://img.shields.io/github/license/lorislab/corn?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/lorislab/corn/build/master?logo=github&style=for-the-badge)](https://github.com/lorislab/corn/actions?query=workflow%3Abuild)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/lorislab/corn?logo=github&style=for-the-badge)](https://github.com/lorislab/corn/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.corn/corn-parent?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.corn/corn-parent)


### Create a release

```bash
mvn semver-release:release-create
```

### Create a patch branch
```bash
mvn semver-release:patch-create -DpatchVersion=x.x.0
```