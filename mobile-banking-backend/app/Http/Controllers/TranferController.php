<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use App\Models\PayBill;
use App\Models\Tranfer;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;

class TranferController extends Controller
{


    public function createTransfer(Request $request)
    {
        // Validate the request data
        try {
            $validatedData = $request->validate([
                'user_id' => 'required|exists:users,id',
                'account_number' => 'required|string|max:255',
                'amount' => 'required|numeric|min:0.01',
                // Remove 'date' and 'time' from validation
            ]);
        } catch (\Illuminate\Validation\ValidationException $e) {
            // Return validation errors with a 422 status code
            return response()->json([
                'error' => 'Validation Error',
                'messages' => $e->errors(),
            ], 422);
        }

        DB::beginTransaction();
        try {
            // Retrieve the user
            $user = User::find($validatedData['user_id']);

            // Check if the user exists
            if (!$user) {
                return response()->json([
                    'error' => 'User Not Found',
                    'message' => 'The specified user does not exist.',
                ], 404);
            }

            // Check if the user has sufficient balance
            if ($user->balance < $validatedData['amount']) {
                return response()->json([
                    'error' => 'Insufficient Balance',
                    'message' => 'The user does not have enough balance to complete the transfer.',
                ], 400);
            }

            // Deduct the amount from the user's balance
            $user->balance -= $validatedData['amount'];
            $user->save();

            // Set the date and time on the backend
            $validatedData['date'] = now()->toDateString(); // Current date
            $validatedData['time'] = now()->toTimeString(); // Current time

            // Create the transfer record
            $transfer = Tranfer::create($validatedData);

            // Create a notification for the user
            Notification::create([
                'user_id' => $user->id,
                'title' => 'Money Transfer Successful',
                'description' => 'Your transfer of ' . $validatedData['amount'] . ' has been processed successfully to account number ' . $validatedData['account_number'] . '.',
                'is_read' => false,
            ]);

            PayBill::create([
                'user_id' => $user->id,
                'bill_type' => 'Money Transfer Successful',
                'account_number' => $validatedData['account_number'],
                'amount' => $validatedData['amount'],
                'date' => $validatedData['date'],
                'time' => $validatedData['time'],
            ]);

            DB::commit();

            // Return the created transfer record with a 201 status code
            return response()->json([
                'message' => 'Transfer successful',
                'data' => $transfer,
            ], 201);

        } catch (\Exception $e) {
            DB::rollBack();
            Log::error('Transfer failed: ' . $e->getMessage());

            return response()->json([
                'error' => 'Internal Server Error',
                'message' => 'An error occurred while processing the transfer. Please try again later.',
            ], 500);
        }
    }
}
