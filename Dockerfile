FROM postgis/postgis:15-3.3

# Add custom initialization scripts if needed
# COPY ./init-scripts/ /docker-entrypoint-initdb.d/

# Set environment variables
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=elmenus_lite

# Expose the PostgreSQL port
EXPOSE 5432