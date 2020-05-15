-- CREATE SCHEMA devist;

CREATE TABLE "devist"."user" (
                        "id" uuid PRIMARY KEY NOT NULL,
                        "name" varchar(2000),
                        "email" varchar(2000),
                        "status" integer,
                        "password_hash" varchar(1000),
                        "email_verification_hash" varchar(1000)
);

CREATE TABLE "devist"."channel" (
                           "id" uuid PRIMARY KEY NOT NULL,
                           "name" varchar(1000),
                           "title" varchar(1000),
                           "sub_title" varchar(4000),
                           "status" integer,
                           "private" boolean,
                           "direct_message" boolean,
                           "created" timestamp,
                           "created_by" uuid
);

CREATE TABLE "devist"."channel_members" (
                                   "channel_id" uuid,
                                   "user_id" uuid,
                                   "last_message_read_datetime" timestamp
);

CREATE TABLE "devist"."message" (
                           "id" uuid PRIMARY KEY NOT NULL,
                           "created_by" uuid,
                           "channel_id" uuid,
                           "message_content" text
);

ALTER TABLE "devist"."channel" ADD FOREIGN KEY ("created_by") REFERENCES "devist"."user" ("id");

ALTER TABLE "devist"."channel" ADD FOREIGN KEY ("id") REFERENCES "devist"."message" ("channel_id");

ALTER TABLE "devist"."user" ADD FOREIGN KEY ("id") REFERENCES "devist"."message" ("created_by");

ALTER TABLE "devist"."channel" ADD FOREIGN KEY ("id") REFERENCES "devist"."channel_members" ("channel_id");

ALTER TABLE "devist"."user" ADD FOREIGN KEY ("id") REFERENCES "devist"."channel_members" ("user_id");
