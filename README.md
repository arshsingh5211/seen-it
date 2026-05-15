## SeenIt Backend

SeenIt is a Spring Boot backend application for tracking movies and shows you have watched. It integrates with the OMDb API to search for content, fetch detailed information, and maintain a personal watchlist with ratings and reviews.

### Overview

The system is designed around three core areas:

- Search: querying OMDb for movies and shows
- ContentMetadata: a persisted copy of movie or show data fetched from OMDb
- WatchedItem: user-specific data such as rating, review, and timestamps

ContentMetadata acts as a persistent cache of external data. WatchedItem represents the user’s interaction with that content.

### Features
- Search for movies and shows using a query string
- Retrieve detailed information for a specific imdbId
- Add a movie or show to a watchlist using imdbId
- Automatically fetch and store metadata from OMDb if not already present
- Prevent duplicate watchlist entries for the same imdbId
- Rate and review watched content
- Retrieve a full watchlist with enriched metadata
- Self-healing metadata lookup if missing from the database 

### Architecture

The backend is split into two main service layers:

- SearchService 
  - Handles direct interaction with OMDb for user-driven queries. 
  - /search endpoint returns a list of results with basic fields
  - /details endpoint returns full metadata for a specific item 
  - Uses DTOs to map external API responses
- WatchService 
  - Handles persistence and aggregation.
  - Stores metadata in the database
  - Stores user-specific watched items
  - Combines metadata and watched data into a single response
  - Avoids repeated external API calls by using a database-backed cache

### API Endpoints
- Search
  - GET /search?query=batman
  - Returns a list of results:
    - imdbId
    - title
    - year
    - type
    - poster
- Get Details
  - GET /details?id=tt1234567
  - Returns full metadata including:
    - title, year, runtime
    - genre, director, actors
    - plot, language, country
    - ratings, metascore, imdb rating
- Add to Watchlist 
  - Adds a movie or show using imdbId
  - Fetches metadata if not already cached
  - Saves a watched item entry
- Get Watchlist
  - Returns a list of watched items enriched with metadata
  - Uses bulk queries to avoid N+1 database calls
  - Performs in-memory join between WatchedItem and ContentMetadata
- Rate and Review
  - Updates a watched item with:
    - rating (1 to 5)
    - review text
    - reviewedAt timestamp
    - updatedAt timestamp
    - 
### Data Model
#### ContentMetadata
Represents external data from OMDb.
- imdbId (primary key)
- title
- year
- director
- actors
- plot
- genre
- runtime
- language
- ratings

#### WatchedItem
Represents user-specific data.
- watchedItemId (primary key)
- imdbId (reference to metadata)
- rating
- review
- watchedAt
- reviewedAt
- updatedAt

### Design Decisions
- Metadata is stored in PostgreSQL instead of using Redis
- Simpler architecture
- Persistent across restarts
- Acts as a long-lived cache of OMDb data
- Search endpoints do not persist data unless explicitly added to watchlist
- Service layer separates external data from user-owned data
- Bulk fetching is used to avoid inefficient database queries
- Metadata lookup is self-healing when missing

### Future Improvements
- Add user support so watchlists are scoped per user
- Add pagination for large watchlists
- Add filtering and sorting options
- Introduce proper exception handling with custom exceptions
- Add controller advice for centralized error handling
- Implement metadata refresh or expiration strategy
- Add integration and unit tests
- Introduce Redis as an optional short-term cache if needed

### Running the Application
1. Start PostgreSQL locally or via Docker
2. Configure database properties in application.yml
3. Run the Spring Boot application (mvn spring-boot:run)

### Notes
- OMDb API key is required and should be configured via ServiceProperties
- This project currently assumes a single global watchlist and does not yet support multiple users