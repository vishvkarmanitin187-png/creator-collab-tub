const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-creatorc-0f001743-2bfe-471b-b659-40e42c3d5a34";

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: "127.0.0.1",
      port: 8085,
      rules: fs.readFileSync("firestore.rules", "utf8"),
      databaseId: DATABASE_ID
    }
  });
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test("Unauthenticated user cannot read or write users collection", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(unauthedDb.collection("users").doc("alice").get());
  await assertFails(
    unauthedDb.collection("users").doc("alice").set({
      userId: "alice",
      name: "Alice",
      channelName: "Alice Tech",
      category: "TECH",
      subscriberRange: "TIER_100K_500K",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Authenticated user can create their own profile and another authenticated user can read it", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore(DATABASE_ID);
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("users").doc("alice").set({
      userId: "alice",
      name: "Alice Verma",
      channelName: "Alice Tech",
      category: "TECH",
      subscriberRange: "TIER_100K_500K",
      createdAt: now,
      updatedAt: now
    })
  );

  // Bob can read Alice's profile (creator discovery)
  const bobDb = testEnv.authenticatedContext("bob").firestore(DATABASE_ID);
  await assertSucceeds(bobDb.collection("users").doc("alice").get());

  // Bob CANNOT update Alice's profile
  await assertFails(
    bobDb.collection("users").doc("alice").update({
      name: "Hacked by Bob",
      updatedAt: now
    })
  );
});

test("Authenticated user can send collab request to target creator", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore(DATABASE_ID);
  const bobDb = testEnv.authenticatedContext("bob").firestore(DATABASE_ID);
  const charlieDb = testEnv.authenticatedContext("charlie").firestore(DATABASE_ID);
  const now = new Date();

  const reqDoc = aliceDb.collection("collab_requests").doc("req_1");

  await assertSucceeds(
    reqDoc.set({
      senderUserId: "alice",
      senderName: "Alice",
      targetCreatorId: "bob",
      targetCreatorName: "Bob",
      collabType: "CROSS_PROMO",
      projectTitle: "Top 10 Tech gadgets",
      status: "PENDING",
      createdAt: now,
      updatedAt: now
    })
  );

  // Bob can read the request sent to him
  await assertSucceeds(bobDb.collection("collab_requests").doc("req_1").get());

  // Alice can read her sent request
  await assertSucceeds(aliceDb.collection("collab_requests").doc("req_1").get());

  // Charlie is not involved and CANNOT read the request
  await assertFails(charlieDb.collection("collab_requests").doc("req_1").get());
});

test("Creator can manage their own projects with cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore(DATABASE_ID);
  const bobDb = testEnv.authenticatedContext("bob").firestore(DATABASE_ID);
  const now = new Date();

  const projDoc = aliceDb.collection("projects").doc("proj_1");

  await assertSucceeds(
    projDoc.set({
      creatorUserId: "alice",
      title: "Duet Video Project",
      partnerName: "Bob",
      collabType: "PODCAST_GUEST",
      status: "IN_PROGRESS",
      createdAt: now,
      updatedAt: now
    })
  );

  // Bob cannot read Alice's internal project tracking pipeline
  await assertFails(bobDb.collection("projects").doc("proj_1").get());

  // Alice can update her project
  await assertSucceeds(
    projDoc.update({
      status: "COMPLETED",
      updatedAt: now
    })
  );
});

test("Private one-to-one messaging isolation between creators", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore(DATABASE_ID);
  const bobDb = testEnv.authenticatedContext("bob").firestore(DATABASE_ID);
  const charlieDb = testEnv.authenticatedContext("charlie").firestore(DATABASE_ID);
  const now = new Date();

  const msgDoc = aliceDb.collection("messages").doc("msg_1");

  // Alice sends message to Bob
  await assertSucceeds(
    msgDoc.set({
      senderId: "alice",
      receiverId: "bob",
      senderName: "Alice Verma",
      text: "Hey Bob! Want to collaborate on a tech video?",
      createdAt: now
    })
  );

  // Bob (receiver) can read the message
  await assertSucceeds(bobDb.collection("messages").doc("msg_1").get());

  // Alice (sender) can read the message
  await assertSucceeds(aliceDb.collection("messages").doc("msg_1").get());

  // Charlie (unrelated third party) CANNOT read Alice & Bob's private message
  await assertFails(charlieDb.collection("messages").doc("msg_1").get());
});
