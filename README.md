# tweetchime

🔔 Simple tweet notifier which supports Discord Webhook

[![Kotlin](https://img.shields.io/badge/Kotlin-1.7-blue)](https://kotlinlang.org)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SlashNephy/tweetchime/Docker)](https://hub.docker.com/r/slashnephy/tweetchime)
[![license](https://img.shields.io/github/license/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/issues)
[![pull requests](https://img.shields.io/github/issues-pr/SlashNephy/tweetchime)](https://github.com/SlashNephy/tweetchime/pulls)

[![screenshot.png](https://i.imgur.com/S7zK0Kl.png)](https://github.com/SlashNephy/tweetchime)

## Requirements

- Java 17 or later

`docker-compose.yml`

```yaml
version: '3.8'

services:
  tweetchime:
    container_name: tweetchime
    image: ghcr.io/slashnephy/tweetchime:master
    restart: always
    volumes:
      - data:/app/data
    environment:
      # 必須: Twitter 資格情報
      TWITTER_CK: xxx
      TWITTER_CS: xxx
      TWITTER_AT: xxx-xxx
      TWITTER_ATS: xxx
      # 必須: Discord Webhook URL
      DISCORD_WEBHOOK_URL: https://discord.com/api/webhooks/xxx

      # ツイートの取得間隔 (秒)
      INTERVAL_SEC: 1800
      # 一度のチェックで通知する最大数
      LIMIT_NOTIFICATIONS: 1
      # ログレベル (OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL)
      LOG_LEVEL: 'TRACE'
      
      # 対象のスクリーンネーム (複数指定可能)
      TARGET_SCREEN_NAMES: 'maaya_taso'
      TARGET_SCREEN_NAMES2: 'AUTOMATONJapan'
      # 対象のユーザ ID (複数指定可能)
      TARGET_USER_IDS: 100000
      # 対象のリスト ID (複数指定可能)
      TARGET_LIST_IDS: 100000001

      # RT を無視するか
      IGNORE_RTS: 1
      # 無視するテキスト (部分一致, 複数指定可能)
      IGNORE_TEXTS: '求人'
      IGNORE_TEXTS2: 'PR'

volumes:
  data:
```
