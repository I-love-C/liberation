PROTO_DIR = proto
OUT_DIR = src/generated
PROTO_FILE := $(PROTO_DIR)/conversion.proto

.PHONY: all clean generate run

all: generate run

clean:
	@rm -rf $(OUT_DIR)
	@mkdir -p $(OUT_DIR)

generate: clean
	protoc                          \
		--scala_out=grpc:$(OUT_DIR) \
		$(PROTO_FILE)

run: generate
	scala run .
