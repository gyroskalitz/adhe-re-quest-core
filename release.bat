@echo off
set /p VER="version(eg. 1.0.0):"

echo Starting release process for v%VER%...

:: 0.确保 Linux 环境下 gradlew 可运行
git update-index --chmod=+x gradlew

:: 1. 提交所有更改
git add .
git commit -m "chore: release v%VER%"

:: 2. 加标签 (本地)
git tag -a v%VER% -m "Release version %VER%"

:: 3. 推送到远程 (触发 GitHub Actions)
git push origin master
git push origin v%VER%

echo Done!
pause