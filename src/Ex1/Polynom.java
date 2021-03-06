package Ex1;

import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import Ex1.Monom;

/**
 * This class represents a Polynom with add, multiply functionality, it also
 * should support the following: 1. Riemann's Integral:
 * https://en.wikipedia.org/wiki/Riemann_integral 2. Finding a numerical value
 * between two values (currently support root only f(x)=0). 3. Derivative
 * 
 * @author Boaz
 *
 */

public class Polynom implements Polynom_able{

	/**
	 * Zero (empty polynom)
	 */

	public Polynom() {}

	/**
	 * init a Polynom from a String such as:
	 *  {"x", "3+1.4X^3-34x", "(2x^2-4)(-1.2x-7.1)", "(3-3.4x+1)((3.1x-1.2)-(3X^2-3.1))"};
	 * @param s: is a string represents a Polynom
	 */
	public Polynom(String s) 
	{
		
		String afterTrimed=s.replaceAll("\\s", ""); // we trimed the String and deleteing the spaces
		if( afterTrimed.isEmpty()) {
			//System.out.println("Polynom is Empty");
			throw new RuntimeException("Invalid Polynom hgjy");
		}
		String [] arr=afterTrimed.split("(\\+)|(?=-)"); // we split the string and add it to array 
		//System.out.println(Arrays.toString(arr));

		for (int j = 0; j < arr.length; j++)
		{
			Monom temp=new Monom(arr[j]);
			this.add(temp);
			if (temp.isCreated==false) 
			{
				//System.out.println("");
				this.polynom.clear();
				throw new RuntimeException("Invalid Polynom");
			}
		}
		this.polynom.sort(Monom._Comp);

	}

	@Override
	public double f(double x) 
	{
		double sum=0;
		Iterator<Monom> it= this.iteretor();
		for(int i=0; i<this.polynom.size(); i++) 
		{
			Monom m=it.next();
			double d= m.f(x);
			sum=sum+d;	   
		}
		return sum;
	}

	@Override
	public void add(Polynom_able p1) 
	{
		Iterator<Monom> it = p1.iteretor();
		while(it.hasNext()) 
		{
			this.add(it.next());
		}
	}

	@Override
	public void add(Monom m1) 
	{	
		for(int i=0; i<this.polynom.size(); i++) 
		{
			if (polynom.get(i).get_power()==m1.get_power()) 
			{
				polynom.get(i).add(m1);
				return;
			}
		}
		polynom.add(m1)   ;  
		polynom.sort(Monom._Comp);
	}

	@Override
	public void substract(Polynom_able p1) {
		
		Iterator<Monom> it = p1.iteretor();
		while(it.hasNext()) 
		{
			Monom m= new Monom(it.next());
			m.multipy(Monom.MINUS1);

			this.add(m);
		}
	}

	@Override
	public void multiply(Polynom_able p1) {

		Polynom temp=new Polynom();

		for(int i=0; i<this.polynom.size(); i++)
		{
			Iterator<Monom> it = p1.iteretor();
			while(it.hasNext())
			{
				Monom m1=new Monom(it.next());
				Monom m=new Monom(polynom.get(i));
				m.multipy(m1);
				temp.add(m);
			}
		}
		this.polynom=temp.polynom;
	}

	@Override
	public boolean equals(Object p1) 
	{
		if(p1 instanceof Monom) {
			Polynom tmp = new Polynom(p1.toString());
			tmp.substract(this); //uses function subtract to check equivalence if result is zero then they are equal
			return tmp.isZero();
		}
		if(p1 instanceof Polynom_able)
		{
			Polynom_able temp=this.copy();
			temp.substract((Polynom_able)p1);
			return temp.isZero();
		}
		return false;
	}

	@Override
	public boolean isZero() 
	{
		for (int i=0; i<this.polynom.size(); i++) 
		{
			if (!(this.polynom.get(i).isZero()))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public double root(double x0, double x1, double eps) {
		if(this.isZero()) {
			return 0;
		}
		double leftX=this.f(x0); //computes f(x) with one bound 
		double rightX=this.f(x1); //computes f(x) with other bound

		if((leftX*rightX)>0) { //if true function doesn't even touch x axis 
			{ throw new RuntimeException("According to Erech Habenaim there is no root");

			}
		}

		if(leftX==0)  //means functions reset at one of the given points
			return x0;//
		if(rightX==0)//
			return x1;//


		double max=Math.max(x0, x1);//right side range
		double min=Math.min(x0, x1);//left side range
		double half=(max+min)/2; 

		while(max-min>eps) 
		{
			if(this.f(half)==0)
				return half;
			if(this.f(max)*this.f(half)<0)  
			{
				max=max;
				min=half;
				half=(max+min)/2;
			}
			else
			{ 
				max=half;
				min=min;	
				half=(max+min)/2;
			}

		}
		return half;
	}

	@Override
	public Polynom_able copy() 
	{
		Polynom_able tmp= new Polynom(); 
		for (int i = 0; i < this.polynom.size(); i++) {
			Monom m = new Monom(this.polynom.get(i));
			tmp.add(m);			
		}
		return tmp;
	}

	@Override
	public Polynom_able derivative()
	{
		Iterator<Monom> iter=this.iteretor();

		Polynom_able tmp= new Polynom(); 
		while(iter.hasNext())
		{
			Monom mo=new Monom(iter.next());
			tmp.add(mo.derivative());
		}	
		return tmp;
	}

	@Override
	public double area(double x0, double x1, double eps)
	{
		double ans=0;
		while (x0<x1) {	
			if(f(x0)>0) {
				ans=ans+(f(x0+eps))*eps;			
			}

			x0=x0+eps;

		}
		return ans;
	}

	@Override
	public Iterator<Monom> iteretor() 
	{
		return polynom.iterator();
	}

	@Override
	public void multiply(Monom m1)  // i have change + sort
	{
		for (int i=0; i<this.polynom.size(); i++) 
		{
			this.polynom.get(i).multipy(m1);

		}
		this.polynom.sort(Monom._Comp);
	}

	public String toString() {
		String ans = "";

		if(this.isZero()) {
			return "0";
		}
		for (int i = 0; i < this.polynom.size(); i++) {
			if(i != 0 && this.polynom.get(i).get_coefficient() >= 0) ans += "+";
			ans += this.polynom.get(i).toString();
		}

		return ans;
	}
	public LinkedList<Monom> polynom=new LinkedList<Monom>();

	@Override
	public function initFromString(String s) {

		Polynom temp= new Polynom(s);
		if(temp.polynom.isEmpty()) {
			System.out.println("No applicable Polynom");
			return null;
		}
		return (function)temp;
	}

}