# `/discover` Cheat Sheet

The full text of the Collaborative Discovery prompt, annotated zone-by-zone.

Slide 15 of the Discovery section teaches the *shape* of this prompt — six
colour-coded zones (one preamble plus five content zones). You edit two of
them per run (ROLE per domain, INPUT per feature); the other four are set up
once and left alone. This handout carries the verbatim text for when you want
to read every word.

File location (when stored as a slash command):
`.claude/commands/discover.md`

Run it with:
`/discover "As a customer, I want to earn cashback on my purchases so that I am rewarded for my loyalty"`

---

## Zone 0 — PREAMBLE  *(tan/gold · stays)*

```yaml
---
allowed-tools: Write
description: Discover feature rules from a user story using Example Mapping
argument-hint: "<user story in quotes>"
---
```

**What it does.** The YAML frontmatter is metadata that Claude Code reads
*before* it runs the prompt body. Three keys do three jobs:

- **`allowed-tools: Write`** — scopes which tools this command may use. For
  `/discover` the only side-effect we want is writing `doc/specs/<feature>.md`,
  so `Write` is all we need. Omit this and the command inherits your full
  tool permissions, which is fine but less tidy.
- **`description:`** — the one-line summary that shows up next to the command
  name in Claude Code's slash-command picker. Keep it under ~60 characters
  and write it in the imperative ("Discover…", "Generate…", "Review…").
- **`argument-hint:`** — placeholder text that Claude Code displays after you
  type `/discover` to remind you what to pass. For `/discover`, the argument
  is a whole user story, so `"<user story in quotes>"` is a good hint.

**What to change.** Set these once when you first create the command and
leave them alone. The only time you'd touch them again is if you expand what
the command does (e.g. adding `Read` to `allowed-tools` if you later want
`/discover` to read an existing spec file before refining it).

**Gotchas.**
- The frontmatter **must be the very first thing in the file** — no leading
  blank line, no BOM, no comment before the opening `---`. Claude Code is
  strict about this.
- YAML is indentation-sensitive. Don't indent the keys. Stick to the
  `key: value` form shown above.
- Quote the `argument-hint` value if it contains spaces or angle brackets,
  or YAML will complain.

---

## Zone 1 — ROLE  *(purple · tweak per domain)*

```text
You are a domain expert in customer loyalty.
Propose rules, examples, counter-examples and questions
using the Example Mapping approach.
Treat the draft rules below as a starting point –
refine, split, or challenge them as needed.
```

**What it does.** Primes Claude with a domain persona and anchors the
expectation that the draft rules are negotiable — not a specification to
rubber-stamp.

**What to change.** Swap `customer loyalty` for your own business area —
`retail pricing`, `clinical trials scheduling`, `shipment tracking`, whatever.
Later in the course we replace this hard-coded line with
`Read CLAUDE.md for project domain context` so the same `/discover` command
works across every project you own.

---

## Zone 2 — INPUT  *(orange · tweak per feature)*

```text
###
## Story
As a customer
I want to earn cashback on my purchases
so that I am rewarded for my loyalty.

## Draft rules (what we already know)
- Standard cashback is 1% of qualifying spend
- Premium members earn 2%
- Monthly cashback is capped at $50 per member
- Refunds must reverse any cashback earned

## Known constraints
- All amounts in USD, round half-up to cent
- "Month" = calendar month, customer timezone
###
```

**What it does.** Three sub-sections inside a `### … ###` fence tell Claude
exactly where the input starts and stops, and separate negotiable material
(draft rules) from hard environmental facts (known constraints).

**What to change.** This is the **only** zone that changes per feature. When
the prompt is stored as the `/discover` slash command, the entire fenced
block is replaced with a single `$ARGUMENTS` placeholder — you then run
`/discover "…your story…"` and Claude Code substitutes it in.

**Sweet spot.** 3–5 draft rules is usually enough to anchor the domain
without crowding out Claude's own thinking. Put soft, challengeable things
under **Draft rules**; put hard environmental facts (currency, rounding,
timezone) under **Known constraints**.

---

## Zone 3 — TASK  *(blue · stays)*

```text
Your task is NOT to write Gherkin or Given/When/Then steps. Instead:
1. Identify rules; each must start with "Should..." or "Must...".
2. Give one or more examples per rule. Use "The one where..."
   notation by default. When a rule's inputs vary independently,
   use a markdown table instead (one column per input, one column
   per output).
3. Give at least one counter-example per rule where a meaningful
   valid edge case exists. A counter-example should be a valid
   business boundary or exclusion, not a bug. A boundary row in a
   table satisfies the counter-example requirement for that rule —
   don't restate it as a separate bullet.
4. List any open questions per rule.
```

**What it does.** Blocks Claude's default reflex (writing Gherkin scenarios)
and replaces it with the four-step Example Mapping discipline: rules,
examples, counter-examples, questions.

**Why each line matters.**
- *"Should / Must"* forces normative phrasing and stops Claude from
  writing rules as observations.
- The *"The one where…" notation* keeps examples in narrative, domain
  language rather than tabular test data — except…
- …when inputs vary independently, in which case the *markdown table*
  clause unlocks the tabular pattern from slide 11.
- The *boundary row* clause stops Claude from duplicating a table's
  boundary rows as separate bullet counter-examples.

**Don't water it down.** Every one of those numbered items earned its
place over slides 9, 10 and 12. Refactoring them will break downstream
quality.

---

## Zone 4 — QUALITY CHECKS  *(red · stays)*

```text
QUALITY CHECKS:
- Use plain business language. No UI steps.
- Each example must cover a distinct business behaviour, rule
  boundary, or decision outcome.
- Do not include examples that differ only in amount, wording,
  merchant name, or channel if the business outcome is the same.
- Cover the normal case first, then only add examples for
  boundaries or genuinely different business outcomes.
- When a rule is expressed as a table, don't also list the same
  scenarios as bullet examples — only add a bullet if it
  introduces a distinct rule, boundary, or business outcome the
  table doesn't capture.
- Prefer one compact table plus one counter-example over several
  repetitive examples.
- Before finalising, remove or merge duplicate examples so the
  final set is minimal but complete.
```

**What it does.** Stops Claude from padding the spec with near-duplicate
examples. Without this zone, a simple rule like *"cashback is capped at $50"*
can easily produce six examples that differ only by merchant name or
purchase amount — all of which collapse to the same business outcome.

**The seven checks, grouped.**
- **Language:** plain business, no UI-level steps (clicks, fields).
- **Distinctness:** each example earns its place by showing a distinct
  behaviour, boundary, or outcome — not by tweaking cosmetic details.
- **Ordering:** normal case first, edge cases second.
- **Table / bullet coordination:** when a rule becomes a table, the
  table is the examples — don't also list them as bullets.
- **Preferred shape:** one compact table plus one counter-example beats
  a flood of repetitive bullets.
- **Final sweep:** merge duplicates before finishing.

**Non-negotiable.** Drop any of these checks and Claude's output will
drift back toward padding. Keep the whole set.

---

## Zone 5 — OUTPUT + SAVE  *(green · stays)*

```text
OUTPUT FORMAT:
- Rule: ...
- Example: The one where...
- Counter-example: The one where...
- Questions: ...

Save the result to doc/specs/<feature>.md
```

**What it does.** Gives Claude an explicit nested template to copy, and
turns the discovery output into a version-controlled file rather than a
chat message that scrolls away.

**The artifact matters.** Every run of `/discover` produces a markdown
file in `doc/specs/` that you can open, edit, commit, and hand to the next
phase (acceptance tests, implementation). The spec lives in the repo where
it belongs, not in Claude's memory.

**File naming.** Claude infers a kebab-case slug from the story — e.g.
`cashback-rewards.md` from *"As a customer, I want to earn cashback on my
purchases"*.

---

## Putting It All Together

The full prompt, zones in order (copy-paste this into `.claude/commands/discover.md`):

```text
---
allowed-tools: Write
description: Discover feature rules from a user story using Example Mapping
argument-hint: "<user story in quotes>"
---
You are a domain expert in customer loyalty.
Propose rules, examples, counter-examples and questions
using the Example Mapping approach.
Treat the draft rules below as a starting point –
refine, split, or challenge them as needed.

###
$ARGUMENTS
###

Your task is NOT to write Gherkin or Given/When/Then steps. Instead:
1. Identify rules; each must start with "Should..." or "Must...".
2. Give one or more examples per rule. Use "The one where..."
   notation by default. When a rule's inputs vary independently,
   use a markdown table instead (one column per input, one column
   per output).
3. Give at least one counter-example per rule where a meaningful
   valid edge case exists. A counter-example should be a valid
   business boundary or exclusion, not a bug. A boundary row in a
   table satisfies the counter-example requirement for that rule —
   don't restate it as a separate bullet.
4. List any open questions per rule.

QUALITY CHECKS:
- Use plain business language. No UI steps.
- Each example must cover a distinct business behaviour, rule
  boundary, or decision outcome.
- Do not include examples that differ only in amount, wording,
  merchant name, or channel if the business outcome is the same.
- Cover the normal case first, then only add examples for
  boundaries or genuinely different business outcomes.
- When a rule is expressed as a table, don't also list the same
  scenarios as bullet examples — only add a bullet if it
  introduces a distinct rule, boundary, or business outcome the
  table doesn't capture.
- Prefer one compact table plus one counter-example over several
  repetitive examples.
- Before finalising, remove or merge duplicate examples so the
  final set is minimal but complete.

OUTPUT FORMAT:
- Rule: ...
    - Example: The one where...
    - Counter-example: The one where...
    - Questions: ...

Save the result to doc/specs/<feature>.md
```

When you run it as a slash command, replace the fenced `$ARGUMENTS` block
with your actual story + draft rules + known constraints:

```text
/discover "As a customer, I want to earn cashback on my purchases
so that I am rewarded for my loyalty.

Draft rules:
- Standard cashback is 1% of qualifying spend
- Premium members earn 2%
- Monthly cashback is capped at $50 per member
- Refunds must reverse any cashback earned

Known constraints:
- All amounts in USD, round half-up to cent
- 'Month' = calendar month, customer timezone"
```

---

## Quick Reference: What Changes, What Stays

| Zone | Changes | Stays |
|---|---|---|
| 0. Preamble | — | `allowed-tools`, `description`, `argument-hint` (set once per command) |
| 1. Role | Domain phrase (per project) | Structure: persona + "refine, split, challenge" |
| 2. Input fence | Story + draft rules + constraints (per feature) | The `###` fence and `## Story / ## Draft rules / ## Known constraints` subheadings |
| 3. Task | — | All four numbered instructions |
| 4. Quality checks | — | All seven dedup / distinctness rules |
| 5. Output + save | — | Output template + `doc/specs/<feature>.md` save path |

**Rule of thumb.** If you find yourself rewriting anything in zones 0, 3, 4,
or 5, stop. You almost certainly want to change zone 1 or zone 2 instead.

---

*This handout accompanies slide 15 of the Collaborative Discovery section.
For the pedagogical walkthrough, see slides 9–12 (building up the prompt),
slide 14 (what slash commands are), slide 15 (the anatomy view), and
slide 16 (creating the command in three steps).*
