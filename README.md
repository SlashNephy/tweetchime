# tweetchime

🔔 Simple tweet notifier which supports Discord Webhook

[![Kotlin](https://img.shields.io/badge/Kotlin-1.4.30-blue)](https://kotlinlang.org)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SlashNephy/tweetchime/Docker)](https://hub.docker.com/r/slashnephy/tweetchime)
[![Docker Image Size (tag)](https://img.shields.io/docker/image-size/slashnephy/tweetchime/latest)](https://hub.docker.com/r/slashnephy/tweetchime)
[![Docker Pulls](https://img.shields.io/docker/pulls/slashnephy/tweetchime)](https://hub.docker.com/r/slashnephy/tweetchime)
[![license](https://img.shields.io/github/license/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/issues)
[![pull requests](https://img.shields.io/github/issues-pr/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/pulls)

[![screenshot.png](https://raw.githubusercontent.com/SlashNephy/tweetchime/master/docs/screenshot.png)](https://github.com/SlashNephy/tweetchime)

## Requirements

- Java 8 or later

## Get Started

`config.yml`

```yaml
# Twitter の資格情報
ck: xxx
cs: xxx
at: xxx
ats: xxx

# ツイートの取得間隔 (秒)
# 10 未満の値はエラーになります
interval: 3600
# 一度のチェックで通知する最大数
limit: 1
# ログレベル (OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL)
logLevel: 'TRACE'

# チェックするツイート定義のリスト
tweets:
    # スクリーンネーム
  - userScreenName: 'AUTOMATONJapan'
    # Discord Webhook URL
    discordWebhookUrl: 'https://discord.com/api/webhooks/xxx/xxx'
    # RT を無視するか
    ignoreRTs: true
    # 無視するテキスト (部分一致)
    ignoreTexts:
      - '【求人】'

    # ユーザ ID
  - userId: 4000001
    discordWebhookUrl: 'https://discord.com/api/webhooks/xxx/xxx'

    # リスト ID
  - listId: 10000001
    discordWebhookUrl: 'https://discord.com/api/webhooks/xxx/xxx'
```

### Docker

There are some image tags.

- `slashnephy/tweetchime:latest`  
  Automatically published every push to `master` branch.
- `slashnephy/tweetchime:dev`  
  Automatically published every push to `dev` branch.
- `slashnephy/tweetchime:<version>`  
  Coresponding to release tags on GitHub.

`docker-compose.yml`

```yaml
version: '3.8'

services:
  tweetchime:
    container_name: tweetchime
    image: slashnephy/tweetchime:latest
    restart: always
    volumes:
      - ./config.yml:/app/config.yml:ro
      - data:/app/data

volumes:
  data:
    driver: local
```
