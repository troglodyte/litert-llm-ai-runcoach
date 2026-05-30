# LiteRT Coach — Project Summary

**Purpose:** On-device AI running/cycling coach. Generates personalized weekly training plans and provides interactive coaching chat — all via an LLM running locally through Google's **LiteRT-LM** runtime.

**Stack:** Kotlin 2.0.21, Jetpack Compose (BOM 2025.05.00), Material3, Hilt DI, Room, WorkManager, Navigation Compose, OkHttp, kotlinx-serialization. AGP 9.2.1, `compileSdk/targetSdk=35`, `minSdk=26`.

**Architecture:** Clean Architecture + MVVM across 3 layers:
- `domain/` — models (`UserProfile`, `TrainingPlan`, `WorkoutLog`, `ChatMessage`), repository interfaces, use cases (`GeneratePlanUseCase`, `SummarizeChatUseCase`, `LogWorkoutUseCase`, etc.)
- `data/` — Room database (6 entities, 4 DAOs), repository implementations with entity↔domain `Mappers`
- `ui/` — Compose screens organized by feature: `onboarding/` (5-step wizard), `plan/`, `history/`, `chat/`, `profile/`, `navigation/`, `shared/`, `theme/`

**AI layer** (`ai/`):
- `CoachModel` interface — `generate(prompt): Flow<String>` for streaming token output
- `Gemma3nModel` — wraps `com.google.ai.edge.litertlm` Engine/Conversation API with 3 model variants (Gemma 3n 1B/4B, Gemma3 1B IT)
- `ModelDownloader` — downloads `.litertlm` files from Hugging Face with progress `Flow`
- `PromptBuilder` — assembles structured prompts from profile/history/plan/chat context

**Flow:** Onboarding → download model → main scaffold with 3 bottom tabs (Plan, History, Profile) + Chat. Weekly auto-plan via `WeeklyPlanWorker`. Chat auto-summarizes after 20 messages.

**Tests:** JUnit + MockK + Turbine for unit tests; Room DAO + Compose UI + Hilt instrumented tests.
