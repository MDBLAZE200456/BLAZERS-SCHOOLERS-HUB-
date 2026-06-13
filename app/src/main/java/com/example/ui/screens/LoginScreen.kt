package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudentProfileEntity
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    savedStudents: List<StudentProfileEntity>,
    onLoginClick: (
        username: String,
        pin: String,
        firstName: String,
        surname: String,
        lastNameOptional: String,
        phoneNumber: String,
        email: String,
        department: String
    ) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var lastNameOptional by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    
    var errorText by remember { mutableStateOf<String?>(null) }
    var isRegisterMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD3EDE0), // Light mint green accent
                        DeepSlateBg // Crisp light mint-white background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant rocket logo emblem
            Image(
                painter = painterResource(id = com.example.R.drawable.ic_app_logo),
                contentDescription = "Blazers Scholar Hub Logo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, PrimaryBlue, RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Titles
            Text(
                text = "BLAZERS SCHOLAR HUB",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSlateSurface,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = "STUDENT TERMINAL & PERSISTENT SQL BACKEND",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Authentication Gate Input Box
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (isRegisterMode) "Register Profile" else "Sign In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isRegisterMode) "Create your comprehensive student profile" else "Enter your student credentials to log in",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Username field (Required always)
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorText = null
                        },
                        label = { Text("Student Username (Login ID)", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = "User Icon", tint = PrimaryBlue)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PIN/Password field (Required always)
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorText = null
                        },
                        label = { Text("Security Access PIN / Pass", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = "Lock Icon", tint = PrimaryBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Required (e.g. 1234)", color = Color(0xFF475569)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    // Expandable additional sign-up fields
                    AnimatedVisibility(
                        visible = isRegisterMode,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFF334155), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "PERSONAL PROFILE DETAILS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // First Name Field
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it; errorText = null },
                                label = { Text("First Name (Required)", color = Color(0xFF94A3B8)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.PersonOutline, contentDescription = "First Name", tint = PrimaryBlue)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_first_name")
                            )

                            // Surname Field
                            OutlinedTextField(
                                value = surname,
                                onValueChange = { surname = it; errorText = null },
                                label = { Text("Surname / Family Name (Required)", color = Color(0xFF94A3B8)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.Badge, contentDescription = "Surname", tint = PrimaryBlue)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_surname")
                            )

                            // Last Name Field (Optional)
                            OutlinedTextField(
                                value = lastNameOptional,
                                onValueChange = { lastNameOptional = it },
                                label = { Text("Last Name (Optional)", color = Color(0xFF475569)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.AccountBox, contentDescription = "Last Name Optional", tint = PrimaryBlue)
                                },
                                placeholder = { Text("Optional second last name", color = Color(0xFF475569)) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_last_name_optional")
                            )

                            // Phone Number Field
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it; errorText = null },
                                label = { Text("Phone Number (Required)", color = Color(0xFF94A3B8)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                leadingIcon = {
                                    Icon(Icons.Filled.Phone, contentDescription = "Phone Number", tint = PrimaryBlue)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_phone_number")
                            )

                            // Email Address Field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; errorText = null },
                                label = { Text("Email Address (Required)", color = Color(0xFF94A3B8)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = "Email", tint = PrimaryBlue)
                                },
                                placeholder = { Text("student@university.edu", color = Color(0xFF475569)) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_email")
                            )

                            // Academic Department / Major Field
                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it; errorText = null },
                                label = { Text("Academic Department / Major (Required)", color = Color(0xFF94A3B8)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                leadingIcon = {
                                    Icon(Icons.Filled.School, contentDescription = "Department", tint = PrimaryBlue)
                                },
                                placeholder = { Text("e.g. Computer Science", color = Color(0xFF475569)) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("signup_department")
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = errorText != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = errorText ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary login / gate action button
                    Button(
                        onClick = {
                            if (username.isBlank()) {
                                errorText = "Username is required to access terminal"
                            } else if (password.isBlank()) {
                                errorText = "Security PIN / Password is required"
                            } else if (isRegisterMode) {
                                if (firstName.isBlank()) {
                                    errorText = "First Name is required"
                                } else if (surname.isBlank()) {
                                    errorText = "Surname is required"
                                } else if (phoneNumber.isBlank()) {
                                    errorText = "Phone Number is required"
                                } else if (email.isBlank()) {
                                    errorText = "Email Address is required"
                                } else if (department.isBlank()) {
                                    errorText = "Academic Department is required"
                                } else {
                                    onLoginClick(
                                        username.trim(),
                                        password.trim(),
                                        firstName.trim(),
                                        surname.trim(),
                                        lastNameOptional.trim(),
                                        phoneNumber.trim(),
                                        email.trim(),
                                        department.trim()
                                    )
                                }
                            } else {
                                // Default basic sign-in
                                onLoginClick(
                                    username.trim(),
                                    password.trim(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isRegisterMode) "REGISTER NOW" else "SIGN IN",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Next Icon",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRegisterMode) "Already have an account? " else "Don't have an account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = if (isRegisterMode) "Sign In" else "Register Now",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            ),
                            modifier = Modifier
                                .clickable {
                                    isRegisterMode = !isRegisterMode
                                    errorText = null
                                    firstName = ""
                                    surname = ""
                                    lastNameOptional = ""
                                    phoneNumber = ""
                                    email = ""
                                    department = ""
                                }
                                .testTag("toggle_auth_mode")
                        )
                    }
                }
            }
        }
    }
}
