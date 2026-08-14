---
name: commit-message
description: Drafts a structured git commit message whenever the user asks to make a commit in this repo. Use this proactively any time you are about to run `git commit` — instead of a bare one-line subject, build the message body from the actual staged diff so it explains why the change matters, what changed, which files changed, and which tests were included. Also invoke on explicit requests like "write a commit message", "draft a commit", or "help me commit this".
---

# Commit Message

Builds the body of a git commit message from the actual staged diff, instead of a bare one-line subject. Follows the repo-wide git safety rules (only commit when explicitly asked, never `-A`/`--amend`/`--no-verify` unless requested, stage specific files by name).

## Steps

1. Gather context (run in parallel):
   - `git status` — see what's staged vs. untracked
   - `git diff --staged` (fall back to `git diff` if nothing is staged yet, and confirm with the user before staging)
   - `git diff --staged --stat` — file list with +/- counts
   - `git log --oneline -10` — match this repo's existing message style/tone

2. From the diff, work out four things:
   - **Why it matters** — the motivation. Pull this from the conversation (what the user asked for and why), or from a referenced `doc/specs/` file if this implements a spec rule. Don't guess if it's not derivable — say what problem the change solves, not just that "code was added."
   - **What changed** — a short functional summary (1-3 sentences), not a restatement of the diff line-by-line.
   - **Files changed** — the file list from `--stat`, grouped by hexagonal layer if the change spans several (domain/application/adapter) since that boundary is meaningful in this codebase.
   - **Tests included** — which test files were added/modified in the diff. If none were, say so explicitly (`No tests included`) rather than omitting the section — a silent gap is worse than a flagged one.

3. Compose the commit message:
   ```
   <concise imperative subject, under 70 chars>

   Why: <why it matters>

   What changed: <functional summary>

   Files changed:
   - <file> — <one-line role, if not obvious from the path>
   ...

   Tests: <test files touched, or "No tests included">

   Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
   ```

4. Create the commit via a HEREDOC (per the global git instructions) so formatting survives:
   ```
   git commit -m "$(cat <<'EOF'
   <message from step 3>
   EOF
   )"
   ```

5. Run `git status` after committing to confirm success. Never amend — if a pre-commit hook fails, fix the issue, re-stage, and create a new commit.
