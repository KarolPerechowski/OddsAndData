# AI usage

## Short summary

This project is being implemented with the **Cursor** desktop agent (Grok 4.6) in an interactive chat. The agent clones this GitHub repository, writes code and documentation, and records user prompts into this file.

A project Cursor hook (`.cursor/hooks.json` + `.cursor/hooks/log_prompt.ps1`) appends each subsequent user prompt to the **Prompt history** section below when this folder is the workspace root. Python was not available on the machine, so the hook is PowerShell.

**Limitation:** `beforeSubmitPrompt` runs for the local Cursor agent. It does not run for cloud agents. Prompts sent before the workspace was moved into this repo are backfilled by hand.

## Artifacts that guided the implementation

- Interview brief (live World Cup scoreboard as a Maven Java package, README + AI.md, extra operation in its own commit)
- Cursor plan: World Cup Scoreboard repo bootstrap (`scoreboard_repo_bootstrap_604d0474`)
- Cursor Hooks documentation (`beforeSubmitPrompt` stdin JSON: `prompt`, optional `conversation_id`)
- Cursor `create-hook` skill (project hooks under `.cursor/hooks.json`)
- Classic live-scoreboard ranking example (Mexico/Canada, Spain/Brazil, Germany/France, Uruguay/Italy, Argentina/Australia) used as an acceptance test for summary ordering

## Prompt history

<!-- prompt-history:start -->

### 2026-10-22 — conversation bootstrap (backfilled)

The hook was not active for these messages (home workspace, then plan mode). They are copied here so the interview `AI.md` requirement is met from the first commit.

**User prompt 1**

> Help me create a plan to implement an interview task i was given:
>
> My interview task is to to implement a simple library that manages a Live Football World Cup Scoreboard.
>
> "Please provide the implementation of the Live Football World Cup Score Board as a simple Java package in a Maven project.
> Provide your solution as a git repository exactly as you would submit it for code review to your team-mates at work. Follow your own standards for high quality software engineering.
> Create a README.md documenting: Your assumptions; Your reasoning; Trade-offs made
> Create a AI.md that includes: Short summary of how AI tools were used; Include your prompt history and other contextual information; Any artifact that guided the implementation
>
> Core Operations (Required): Start a new match; Update the score; Finish a match; Get a summary of matches in progress (total score descending, then most recently started).
> Add exactly one additional operation of your choice.
>
> first step to do is to generate git repo with README.md and AI.md and connect it to the public github repo.
> Configure this so my prompts are automatically included in section in AI.md
> Then having this generate me plan on what next steps should be. We will work the details and changes after that"

**User prompt 2** (plan answers)

> Project name: `worldcup-scoreboard`
> GitHub: existing public repo (URL provided next)
> Remote: `https://github.com/KarolPerechowski/OddsAndData`

**User prompt 3**

> Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.
>
> To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.


### 2026-10-21T22:47:00Z (conversation test-1)

```text
hello from hook test
```

### 2026-10-22T23:00:00Z (conversation bootstrap after workspace move)

```text
Help me create a plan to implement an interview task i was given:
[same interview brief as prompt 1, re-sent after the workspace moved into OddsAndData]
```

<!-- prompt-history:end -->
