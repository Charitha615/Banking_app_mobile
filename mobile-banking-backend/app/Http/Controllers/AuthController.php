<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use App\Models\PayBill;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        // Validate input
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8',
            'user_type' => 'required|string|max:255',
            'account_number' => 'required|string|max:255',
            'branch' => 'required|string|max:255',
            'phone' => 'required|string|max:255',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors(),
            ], 422);
        }

        // Create user
        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'user_type' => $request->user_type,
            'account_number' => $request->account_number,
            'branch' => $request->branch,
            'phone' => $request->phone,
            'password' => Hash::make($request->password),
            'balance' => 1000,
        ]);

        // Generate token
        $token = $user->createToken('LaravelAuthApp')->plainTextToken;


        // Return success response
        return response()->json([
            'success' => true,
            'message' => 'User registered successfully',
            'data' => [
                'user' => [
                    'id' => $user->id,
                    'name' => $user->name,
                    'email' => $user->email,
                    'user_type' => $user->user_type,
                ],
                'token' => $token,
            ],
        ], 201);
    }


    public function login(Request $request)
    {
        // Validate input
        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
            'password' => 'required|string|min:8',
        ]);

        if ($validator->fails()) {
            return response()->json(['error' => $validator->errors()], 422);
        }

        // Attempt authentication
        $credentials = $request->only('email', 'password');
        if (auth()->attempt($credentials)) {
            $user = auth()->user();

            // Revoke all existing tokens for the user
            $user->tokens()->delete();

            // Generate a new token
            $token = $user->createToken('LaravelAuthApp')->plainTextToken;


            return response()->json([
                'message' => 'Login successful',
                'token' => $token,
                'user' => [
                    'id' => $user->id,
                    'name' => $user->name,
                    'email' => $user->email,
                    'user_type'=>$user->user_type,
                    'account_number'=>$user->account_number,
                    'branch'=>$user->branch,
                    'balance'=>$user->balance,
                ],
            ], 200);
        } else {
            return response()->json(['error' => 'Invalid credentials'], 401);
        }
    }

    public function logout(Request $request)
    {
        // Revoke the user's current access token
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logout successful'
        ], 200);
    }


    public function getAllUsers()
    {
        $users = User::all(); // Directly fetch all users

        return response()->json(['users' => $users], 200);
    }

    public function getUserById($id)
    {
        $user = User::find($id); // Fetch user by ID

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        return response()->json(['user' => $user], 200);
    }

    public function deleteUserById($id)
    {
        $user = User::find($id); // Find the user by ID

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        $user->delete(); // Delete the user

        return response()->json(['message' => 'User deleted successfully'], 200);
    }

    public function updateUser(Request $request, $id)
    {
        // Find the user by ID
        $user = User::find($id);

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        // Validate incoming data
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $id,
            'phone' => 'required|string|max:255',
        ]);

        // Update user fields
        $user->update([
            'name' => $request->input('name'),
            'email' => $request->input('email'),
            'phone' => $request->input('phone'),
        ]);

        return response()->json(['message' => 'User updated successfully', 'user' => $user], 200);
    }

    public function updateBalance(Request $request, $id)
    {
        // Find the user by ID
        $user = User::find($id);

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        // Validate incoming data
        $request->validate([
            'balance' => 'required|numeric|min:0|max:9999999.99',
        ]);

        // Update user balance
        $user->balance = $request->balance;

        Notification::create([
            'user_id' => $user->id,
            'title' => 'Top-Up Successful',
            'description' => "A top-up of {$request->balance} has been added successfully to account number {$user->account_number}.",
            'is_read' => false,
        ]);

        // Log the top-up transaction in PayBill history
        PayBill::create([
            'user_id' => $user->id,
            'bill_type' => 'Money Top-Up Successful',
            'account_number' => $user->account_number,
            'amount' => $request->balance,
            'date' => now()->toDateString(),
            'time' => now()->toTimeString(),
        ]);

        if ($user->save()) {
            return response()->json(['message' => 'User balance updated successfully', 'user' => $user], 200);
        }

        return response()->json(['message' => 'Failed to update user balance'], 500);
    }




}
