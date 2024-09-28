FROM golang:1.23-alpine

# set destination for COPY
WORKDIR /app

# copy everything to the working directory
COPY . .

# download go modules
RUN go mod download

# build
RUN GOOS=linux go build -o main cmd/main.go

# start
CMD ["./main"]