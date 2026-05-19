import crypto from "node:crypto";

export function verifyGitHubSignature(
  rawBody: Buffer, // this is the actual content of webhook
  secret: string, //this is the secret key between github and our app
  signature256: string | undefined, // this
): boolean {
  if (!signature256?.startsWith("sha256=")) {
    return false;
  }

  const expected = `sha256=${crypto.createHmac("sha256", secret).update(rawBody).digest("hex")}`;
  const expectedBuffer = Buffer.from(expected, "utf8");
  const receivedBuffer = Buffer.from(signature256, "utf8");
  // console.log(expected);
  if (expectedBuffer.length !== receivedBuffer.length) {
    return false;
  }
  return crypto.timingSafeEqual(expectedBuffer, receivedBuffer);
}
// sample testing.
// const raw = Buffer.from("sddsds");
// const sec = "ssddffssddsf";
// const sign =
//   "sha256=5fdcacaf288407a87fd3308a0650dda028ec4c86f76d459fd817b323b82be1d1";

// const a = verifyGitHubSignature(raw, sec, sign);
// if (a === true) {
//   console.log("ok");
// } else {
//   console.log("no");
// }
// console.log(a);
