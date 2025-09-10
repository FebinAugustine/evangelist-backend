//import io.jsonwebtoken.Jwts
//import java.util.Base64
//
//fun main() {
//    // 1. Create a new, secure 256-bit secret key
//    val secretKey = Jwts.SIG.HS256.key().build()
//
//    // 2. Get the raw bytes of the key
//    val keyBytes = secretKey.encoded
//
//    // 3. Use the STANDARD Base64 encoder
//    val base64Secret = Base64.getEncoder().encodeToString(keyBytes)
//
//    // 4. Print the secret to the console
//    println("Generated Standard Base64 Secret Key:")
//    println(base64Secret)
//}