# Hierarchical Threading Prototype

Status: experimental design and benchmark plan. This branch must remain isolated until measurements justify a merge.

## Goal

Evaluate whether hierarchical task prioritization and adaptive admission improve frame pacing and chunk-work efficiency on low-core-count CPUs, especially 2 physical cores / 4 logical CPUs. Do not assume a win from CPU utilization alone.

## Constraints

- Preserve existing rendering and task correctness.
- Do not change `dev` or merge into it during prototyping.
- Avoid spawning and destroying threads dynamically; reuse a bounded worker pool.
- Keep scheduling decisions cheap and avoid per-task allocation/locking where practical.
- Never starve lower-priority work.
- Respect existing cancellation, shutdown, and chunk-build lifecycle semantics.

## Proposed hierarchy

1. **Urgency classification:** visible/near-player rebuilds, normal nearby work, distant/speculative work, and maintenance.
2. **Admission controller:** use queue depth and recent main-thread/frame-time pressure to defer optional work when overloaded.
3. **Bounded worker execution:** retain reusable workers; start with conservative parallelism and benchmark worker counts rather than assuming four workers is best.
4. **Fairness:** age queued tasks and apply bounded priority boosts so distant work eventually runs.
5. **Observability:** measure queue wait, task execution, completed/cancelled work, worker utilization, and scheduling overhead.

Priority should be based on existing task metadata where possible. Avoid adding expensive distance calculations to every queue operation; cache or calculate priority at task submission/reprioritization boundaries.

## Benchmark protocol

Compare the unmodified parent commit against this branch using the same machine, Java version, game version, world, render distance, graphics settings, and repeatable route/workload.

Record at least:
- Median, p95, and p99 frame time; 1% lows if the capture tool reports them reliably.
- Chunk build completion latency and queue wait time.
- Number of completed, cancelled, and stale builds.
- CPU utilization and per-thread CPU time where available.
- Scheduling overhead and worker idle time.
- Time to settle after a fixed chunk-loading run.

Run each configuration several times, alternate baseline/prototype runs, and report variance. Do not merge unless improvements are repeatable and correctness is unchanged. A reduction in CPU usage alone is not a success if chunk latency or frame pacing worsens.

## Initial experiment matrix

- Baseline scheduler, current worker count.
- Hierarchical scheduler with conservative worker count.
- Worker count 1, 2, and 3 where supported by the implementation.
- Adaptive admission enabled/disabled, changing one variable at a time.

## Implementation notes

Before changing scheduler code, trace the existing `ChunkJobQueue`, `ChunkBuilder`, task submission/cancellation, and rebuild prioritization paths. Prefer the smallest integration point that preserves existing semantics. Keep a simple fallback path for A/B testing if feasible.
