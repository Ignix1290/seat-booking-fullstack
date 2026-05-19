import { useEffect, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'
import UserRegistration from './UserRegistration'

function App() {

  const [seats, setSeats] = useState();
  const [currentUser, setCurrentUser] = useState(null);

  //console.log(seats);

    async function fetchApi(){
    try {
      const storedUser = localStorage.getItem('token'); 
      
      let token = null;
      if (storedUser) {
        console.log("Stored User :", storedUser);
        token = storedUser; 
      }

      const response = await fetch('http://localhost:8080/api/seats', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` 
        }
      });

      if(response.ok){
        let data = await response.json();
        const seatsWithIsSelected = data.map(seat => ({...seat, isSelected : false}));
        setSeats(seatsWithIsSelected);
      } else {
        console.log("Error Status:", response.status); 
        console.log("Status Text:", response.statusText);
      }
    }
    catch(error){
      console.log("Network or Parsing Error:", error);
    }   
  }

  const handleUserCreated = (userData) => {
    if(!userData) return;
    setCurrentUser(userData);
    localStorage.setItem("currentUser", JSON.stringify(userData));
    console.log("app knows who the user is :", userData.name);
  }

  useEffect(()=>{
      fetchApi();
      const storedUser = JSON.parse(localStorage.getItem("currentUser"));
      setCurrentUser(storedUser);
  },[])

  async function handleSeatClick(seatId){
    
    const clickedSeat = seats && seats.map(s => {
      if(s.id === seatId){
        return {...s, isSelected : !s.isSelected};
      }
      return s;
    });
    setSeats(clickedSeat);
    //console.log("clickedSeat", clickedSeat);
    //console.log("seats", seats);
  }

  async function seatBookingFunction(){
    let selectedSeats = seats && seats.filter(s => s.isSelected);
    //console.log("Selected Seats", selectedSeats);
    const token = localStorage.getItem("token");
    
    for(let seat of selectedSeats){
      let newStatus = seat.status === "available" ? "booked" : "available";
      try{
        let response = await fetch(`http://localhost:8080/api/seats/update-seat/${seat.id}`, {
                       method : "PUT", 
                       headers : {"Content-Type" : "application/json",
                                  "Authorization" : `Bearer ${token}`
                       },
                       body : JSON.stringify({id : seat.id, status : newStatus, price : seat.price, user : {"id" : currentUser?.id}})
        });

        if(response.ok){
          let updatedSeatFromServer = await response.json();
          setSeats(prevSeats => prevSeats.map(s => s.id === seat.id ? {...updatedSeatFromServer, isSelected : false} : s));
        }
      }
      catch(error){
        console.log(error);
      }
    }
  }

  async function resetFunction(){
      const token = localStorage.getItem("token");
      try{
        let response = await fetch(`http://localhost:8080/api/seats/reset-all`, {
                       method : "PUT",
                       headers : {"Content-Type" : "application/json",
                                  ...(token && token !== "null" && token !== "undefined" ? { "Authorization": `Bearer ${token}` } : {})
                       },
        });

        if(response.ok) {
            setSeats(prevSeats => 
              prevSeats.map(s => ({...s, status: "available" }))
            );
          }
      }
      catch(error){
        console.log("seats not resetted", error);
      }
    
  }

  let totalPrice = seats && seats.filter(s => s.isSelected).reduce((sum, seat) => sum + seat.price, 0);

  async function addSeat(){
    const token = localStorage.getItem("token");
    try{
      let response = await fetch("http://localhost:8080/api/seats/add-seat", {
        method : "POST",
        headers : {...(token && token !== "null" && token !== "undefined" ? { "Authorization": `Bearer ${token}` } : {})}
      });

      if(response.ok){
        fetchApi();
      }
    }
    catch(error){
      console.log(error);
    }
  }

  async function clearTheater(){
    const token = localStorage.getItem("token");
    try{
      let response = await fetch("http://localhost:8080/api/seats/delete-seats", {
        method : "DELETE",
        headers : {...(token && token !== "null" && token !== "undefined" ? { "Authorization": `Bearer ${token}` } : {})}
      });

      if(response.ok){
        fetchApi();
      }
    }
    catch(error){
      console.log(error);
    }
  }
  //console.log("Current Seats in State:", seats);

  async function cancelSeat(seatId){
        const token = localStorage.getItem("token");
        try{
        let response = await fetch(`http://localhost:8080/api/seats/cancel-seat/${seatId}`, {
                       method : "PUT",
                       headers : {"Content-Type" : "application/json",
                                  ...(token && token !== "null" && token !== "undefined" ? { "Authorization": `Bearer ${token}` } : {})
                       }
        });

        if(response.ok){
          setSeats(prevSeats=> prevSeats.map(p=>p.id === seatId ? {...p, status : "available", user : null} : p));
        }
        }catch(error){
        console.log("Seat couldn't be cancelled", error);
        }
  }

  return (
  <>
    {!currentUser ? (
      <UserRegistration onUserCreated={handleUserCreated} />
    ) : (
      <>
        <div id='seat-container'>
          <h2>Welcome, {currentUser.name}!</h2>
          <div id='seats'>
            {seats && seats.map(seat => (
              <button 
                key={seat.id} 
                className={seat.isSelected ? "isSelected" : ""}
                disabled={seat.status === "booked" && seat.user?.id !== currentUser?.id} 
                onClick={()=>{
                  if(seat.status === "booked" && seat.user?.id === currentUser?.id){
                    cancelSeat(seat.id);
                  } else {
                    handleSeatClick(seat.id);
                  }
                }}
              >
                {seat.status === "booked" ? seat.user?.name : seat.status}
              </button>
            ))}
          </div>
        </div>

        <h3 className='amount'>Total price : {totalPrice}</h3>

        <div className='seat-buttons'>
          <button className='book-button' onClick={seatBookingFunction} disabled={seats && !seats.some(s=>s.isSelected)}>
            Book Seats
          </button>
          <button className='reset-button' onClick={resetFunction} disabled={seats && !seats.some(s=>s.status === "booked")}>
            RESET
          </button>
          <button onClick={addSeat} className='addSeat-button'>Add 1 Seat</button>
          <button onClick={clearTheater} className='removeSeat-button'>Nuke Theater</button>
          
          {/* Logout Button to clear the token */}
          <button onClick={() => { localStorage.clear(); setCurrentUser(null); }} className='logout-button'>
            Logout
          </button>
        </div>
      </>
    )}
  </>
  )
}

export default App
