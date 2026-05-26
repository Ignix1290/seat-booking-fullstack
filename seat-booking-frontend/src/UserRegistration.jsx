import React, { useState } from "react";
import './App.css'

export default function UserRegistration(props){

    const [userName, setUserName] = useState("");
    const [userEmail, setUserEmail] = useState("");

    async function handleRegister(){
        // console.log("Username :", userName);
        // console.log("Useremail :", userEmail);
        try{
            let response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/register`, {
                            method : "POST",
                            headers : {"Content-Type" : "application/json"},
                            body : JSON.stringify({name : userName, email : userEmail})
            });

            if(response.ok){
                let userData = await response.json();
                console.log("UserData :", userData);
                localStorage.setItem("token", userData.token);
                localStorage.setItem("userData", JSON.stringify(userData));
                props.onUserCreated({id : userData.id, name : userName, token : userData.token});
                //console.log("Userdata :", userData);
                //console.log("Success! Sent to App:", savedUser);
                console.log("Token received and saved:", userData.token);
                alert("Registration Successful! Now try to refresh and see the seats.");
            }
        }
        catch(error){
            console.log("Couldn't get userdata :", error);
        }
        
    }
    
    return(
        <>
        <div id="registration-container">
            <h2>User Registration</h2>
            <input type="text" placeholder="Enter Name" onChange={e=> setUserName(e.target.value)}/>
            <input type="email" placeholder="Enter Email" onChange={e=> setUserEmail(e.target.value)}/>
            <button onClick={handleRegister}>Register</button>
        </div>
        </>
    )
}