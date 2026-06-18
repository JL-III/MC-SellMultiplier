# Sell Multiplier
### Permission-based, additive `/sell` multiplier for Theatria

Multiplies money earned via Essentials sell commands based on the
`sell.multiplier.*` permissions a player has. Every matching permission
contributes a configurable bonus, and **all bonuses stack additively**.

## How it works
On an Essentials `COMMAND_SELL` balance update, the plugin sums the configured
value of every `sell.multiplier.*` permission the player holds and applies:

```
payout = base_proceeds * (1 + sum_of_matching_permission_values)
```

For example, a player holding `sell.multiplier.weekend` (0.25) plus the five
`sell.multiplier.community-goal-N` perms (0.10 each) sells for
`1 + 0.25 + 0.50 = 1.75` — a **+75%** bonus.

## Integration with TheatriaSessions
This plugin only *reads* permissions; it never grants them. The
[TheatriaSessions](https://github.com/JL-III/TheatriaSessions) plugin grants
temporary `sell.multiplier.community-goal-N` permissions to the LuckPerms
`default` group as the community reaches daily playtime milestones. The two
plugins are intentionally decoupled — the permission namespace is the contract.

The **percentage each permission is worth is defined here, in this plugin's
config** (the single source of truth). TheatriaSessions only decides *which*
node to grant and *for how long*.

## Permissions
```
sell.multiplier.<key>      Grants the bonus configured for <key> (additive).
sell-multiplier.check.self Allows a player to check their own multipliers.
sell-multiplier.check.other Allows checking another player's multipliers.
```

## Commands
```
/multiplier               - Lists your current sell multipliers and their values.
/multiplier check <player> - Lists another player's multipliers.
```

## Config
```yaml
# Only the permissions listed below grant a bonus. Values are decimals
# (0.25 = +25%). Unlisted or false permissions are ignored.

sell-multipliers:
  # Event multiplier (set manually during events).
  weekend: 0.25

  # Community activity goals (granted automatically by TheatriaSessions).
  community-goal-1: 0.10
  community-goal-2: 0.10
  community-goal-3: 0.10
  community-goal-4: 0.10
  community-goal-5: 0.10
```
